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
import one.spaceman.spiffywidget.data.AlarmAdapter
import one.spaceman.spiffywidget.data.CalendarAdapter
import one.spaceman.spiffywidget.data.LocationAdapter
import one.spaceman.spiffywidget.data.weather.WeatherAdapter
import one.spaceman.spiffywidget.state.SpiffyWidgetState
import one.spaceman.spiffywidget.state.SpiffyWidgetStateDefinition
import one.spaceman.spiffywidget.widget.SpiffyWidget
import one.spaceman.spiffywidget.worker.WidgetWorkManager.PartialUpdate
import kotlin.enums.enumEntries
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

// Coroutine task to get/update widget state
internal class WidgetWorker(
    private val context: Context, workParams: WorkerParameters
) : CoroutineWorker(context, workParams) {

    companion object {
        private const val MAXIMUM_RETRIES = 3
        private const val WEATHER_INTERVAL = 10 // time between weather updates in minutes
        const val TAG = "spiffy-worker"
    }

    @RequiresApi(Build.VERSION_CODES.CINNAMON_BUN)
    override suspend fun doWork(): Result {
        if (runAttemptCount >= MAXIMUM_RETRIES) return Result.failure()
        val update = inputData.getNullableStringArray("parts")?.toList() ?: enumEntries<PartialUpdate>().map { it.name }

        return try {
            val now = Clock.System.now()
            val glanceIds = getGlanceIds()
            var state = getWidgetState(glanceIds)

            if (update.contains("WEATHER")) {
                if (state.weather == null || state.weather.lastUpdate.plus(WEATHER_INTERVAL.minutes) > now) {
                    val locationClient = LocationServices.getFusedLocationProviderClient(context)
                    val location = LocationAdapter.get(context, locationClient)
                    if (location != null && location.isComplete) {
                        val geocode = LocationAdapter.geocode(context, location)
                        val weather = WeatherAdapter.getFormatedWeather(
                            latitude = location.latitude,
                            longitude = location.longitude,
                        )
                        if (weather != null) {
                            state = state.copy(
                                weather = weather.copy(location = geocode)
                            )
                        }
                    }
                }
            }

            if (update.contains("ALARM")) {
                state = state.copy(alarm = AlarmAdapter.get(context))
            }

            if (update.contains("CALENDAR")) {
                state = state.copy(events = CalendarAdapter.get(context))
            }

            setWidgetState(glanceIds, state.copy(lastUpdate = now))
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
                context = context,
                definition = SpiffyWidgetStateDefinition,
                glanceId = glanceId,
                updateState = { newState }
            )
        }
        SpiffyWidget().updateAll(context)
    }
}