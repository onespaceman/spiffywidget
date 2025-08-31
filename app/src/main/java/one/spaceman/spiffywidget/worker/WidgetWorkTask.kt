package one.spaceman.spiffywidget.worker

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import one.spaceman.spiffywidget.SpiffyWidget
import one.spaceman.spiffywidget.data.AlarmAdapter
import one.spaceman.spiffywidget.data.CalendarAdapter
import one.spaceman.spiffywidget.data.LocationAdapter
import one.spaceman.spiffywidget.data.SystemInfo
import one.spaceman.spiffywidget.data.weather.WeatherAdapter
import one.spaceman.spiffywidget.state.SpiffyWidgetState
import one.spaceman.spiffywidget.state.SpiffyWidgetStateDefinition
import one.spaceman.spiffywidget.worker.WidgetWorkManager.Companion.PartialUpdate

internal class WidgetWorkTask(
    private val context: Context,
    workParams: WorkerParameters
) : CoroutineWorker(context, workParams) {

    companion object {
        private const val MAXIMUM_RETRIES = 3
        private const val MIN_WEATHER_INTERVAL = 900 // minimum time between weather updates - 15min
        const val TAG = "spiffy-worker"
    }

    private val locationClient = LocationServices.getFusedLocationProviderClient(context)

    override suspend fun doWork(): Result {
        if (runAttemptCount >= MAXIMUM_RETRIES) return Result.failure()
        val glanceIds = getGlanceIds()
        val update =
            inputData.getStringArray("partialUpdate")?.toList() ?: PartialUpdate.getStringArray()

        return try {
            var newState = getWidgetState(glanceIds)

            val systemInfo = SystemInfo()
            val location = LocationAdapter.get(context, locationClient)

            if (location != null && location.isComplete) {
                val address = LocationAdapter.geocode(context, location)
                if (address != null) {
                    newState = newState.copy(location = "${address.locality}, ${address.adminArea}")
                }

                if (update.contains("WEATHER")) {
                    if (newState.weather == null || systemInfo.now.epochSecond - newState.weather.lastUpdate > MIN_WEATHER_INTERVAL) {
                        val weather = WeatherAdapter.getFormatedWeather(
                            context = context,
                            info = systemInfo,
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                        if (weather != null) {
                            newState = newState.copy(weather = weather)
                        } else if (newState.weather != null) {
                            newState = newState.copy(
                                weather = newState.weather.copy(outdated = true)
                            )
                        }

                    }
                }
            }

            if (update.contains("ALARM")) {
                newState = newState.copy(
                    alarm = AlarmAdapter.get(context, systemInfo)
                )
            }

            if (update.contains("EVENTS")) {
                newState = newState.copy(
                    events = CalendarAdapter.get(context, systemInfo)
                )
            }

            setWidgetState(glanceIds, newState)
            Log.i("Spiffy Widget", "Updated Spiffy Widget with $update")
            Result.success()
        } catch (e: Exception) {
            Log.e("Spiffy-Worker", e.message.toString())
            setWidgetState(glanceIds, SpiffyWidgetState())
            Result.retry()
        }
    }

    private suspend fun getGlanceIds(): List<GlanceId> {
        val manager = GlanceAppWidgetManager(context)
        return manager.getGlanceIds(SpiffyWidget::class.java)
    }

    private suspend fun getWidgetState(glanceIds: List<GlanceId>): SpiffyWidgetState {
        return getAppWidgetState(
            context = context,
            definition = SpiffyWidgetStateDefinition,
            glanceId = glanceIds.first(),
        )
    }

    private suspend fun setWidgetState(
        glanceIds: List<GlanceId>,
        newState: SpiffyWidgetState
    ) {
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                definition = SpiffyWidgetStateDefinition,
                glanceId = glanceId,
                updateState = { newState }
            )
        }
        SpiffyWidget().updateAll(context)
    }
}