package one.spaceman.spiffywidget.worker

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import one.spaceman.spiffywidget.worker.WidgetWorkManager.PartialUpdate
import kotlin.enums.enumEntries

// Coroutine task to get/update widget state
internal class WidgetWorker(
    private val context: Context, workParams: WorkerParameters
) : CoroutineWorker(context, workParams) {

    companion object {
        private const val MAXIMUM_RETRIES = 3
        private const val WEATHER_INTERVAL = 900 // time between weather updates - 15min
        const val TAG = "spiffy-worker"
    }

    @RequiresApi(Build.VERSION_CODES.CINNAMON_BUN)
    override suspend fun doWork(): Result {
        if (runAttemptCount >= MAXIMUM_RETRIES) return Result.failure()
        val update = inputData.getStringArray("parts")?.toList() ?: enumEntries<PartialUpdate>().map { it.name }

        return try {
            val info = SystemInfo()
            val glanceIds = getGlanceIds()
            val oldState = getWidgetState(glanceIds)
            var newState = oldState.copy()

            val locationClient = LocationServices.getFusedLocationProviderClient(context)
            val location = LocationAdapter.get(context, locationClient)
            if (location != null && location.isComplete) {
                val geocode = LocationAdapter.geocode(context, location)
                newState = newState.copy(
                    weather = newState.weather?.copy(
                        location = geocode
                    )
                )
            }

            if (update.contains("WEATHER")) {
                if (oldState.weather == null || info.now.epochSecond - oldState.weather.lastUpdate > WEATHER_INTERVAL) {
                    val locationClient = LocationServices.getFusedLocationProviderClient(context)
                    val location = LocationAdapter.get(context, locationClient)
                    if (location != null && location.isComplete) {
                        val geocode = LocationAdapter.geocode(context, location)
                        val weather = WeatherAdapter.getFormatedWeather(
                            context = context,
                            info = info,
                            latitude = location.latitude,
                            longitude = location.longitude,
                        )
                        if (weather != null) {
                            newState = newState.copy(weather = weather.copy(location = geocode))
                        }
                    }
                }
            }

            if (update.contains("ALARM")) {
                newState = newState.copy(alarm = AlarmAdapter.get(context, info))
            }

            if (update.contains("CALENDAR")) {
                newState = newState.copy(events = CalendarAdapter.get(context))
            }

            setWidgetState(glanceIds, newState)
            Log.i("Spiffy Widget", "Updated Spiffy Widget with $update")
            Result.success()
        } catch (e: Exception) {
            Log.e("Spiffy-Worker", "Failed to update Spiffy Widget " + e.message.toString())
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
        glanceIds: List<GlanceId>, newState: SpiffyWidgetState
    ) {
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context, definition = SpiffyWidgetStateDefinition, glanceId = glanceId, updateState = { newState })
        }
        SpiffyWidget().updateAll(context)
    }
}