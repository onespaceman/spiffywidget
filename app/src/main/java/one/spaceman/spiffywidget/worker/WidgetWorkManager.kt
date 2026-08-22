package one.spaceman.spiffywidget.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.util.concurrent.TimeUnit

class WidgetWorkManager(private val context: Context) {
    enum class PartialUpdate {
        ALARM, EVENTS, WEATHER;
    }

    fun scheduleUpdate() {
        val work = PeriodicWorkRequestBuilder<UpdateSpiffyState>(15, TimeUnit.MINUTES).addTag(
                UpdateSpiffyState.TAG
            ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "spiffy_refresh", ExistingPeriodicWorkPolicy.REPLACE, work
        )
    }

    fun updateNow(
        parts: Array<PartialUpdate> = arrayOf(
            PartialUpdate.ALARM,
            PartialUpdate.EVENTS,
            PartialUpdate.WEATHER
        )
    ) {
        val data =
            Data.Builder().putStringArray("parts", parts.map { it.name }.toTypedArray()).build()
        val work = OneTimeWorkRequestBuilder<UpdateSpiffyState>().addTag(UpdateSpiffyState.TAG)
            .setInitialDelay(Duration.ofSeconds(3)).setInputData(data).build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "spiffy_refresh_now", ExistingWorkPolicy.APPEND, work
        )
    }

    fun cancel() {
        WorkManager.getInstance(context).cancelAllWorkByTag(UpdateSpiffyState.TAG)
    }
}