package one.spaceman.spiffywidget.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.util.concurrent.TimeUnit

class WidgetWorkManager(private val context: Context) {

    fun updateNow(partialUpdate: PartialUpdate? = null) {
        WorkManager.getInstance(context).enqueueUniqueWork(
            UPDATENOWNAME,
            ExistingWorkPolicy.REPLACE,
            getUpdateNowRequest(partialUpdate)
        )
    }

    fun scheduleUpdate() {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SCHEDULEDUPDATENAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            getScheduledUpdateRequest()
        )
    }

    fun cancel() {
        WorkManager.getInstance(context).run {
            cancelUniqueWork(UPDATENOWNAME)
            cancelUniqueWork(SCHEDULEDUPDATENAME)
        }
    }

    private fun getUpdateNowRequest(partialUpdate: PartialUpdate?): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<WidgetWorkTask>()
            .addTag(WidgetWorkTask.TAG)
            .setInitialDelay(Duration.ofSeconds(3L))
            .setInputData(partialUpdateBuilder(partialUpdate))
            .build()
    }

    private fun getScheduledUpdateRequest(): PeriodicWorkRequest {
        return PeriodicWorkRequestBuilder<WidgetWorkTask>(15L, TimeUnit.MINUTES)
            .addTag(WidgetWorkTask.TAG)
            .setInitialDelay(15, TimeUnit.MINUTES)
            .build()
    }

    private fun getDRMConstraints(): Constraints {
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
    }

    private fun partialUpdateBuilder(partialUpdate: PartialUpdate?): Data {
        val data = Data.Builder()
        if (partialUpdate != null) {
            data.putStringArray("partialUpdate", arrayOf(partialUpdate.toString()))
        }
        return data.build()
    }

    companion object {

        private const val UPDATENOWNAME = "update_now"
        private const val SCHEDULEDUPDATENAME = "update_scheduled"

        enum class PartialUpdate {
            ALL,
            ALARM,
            EVENTS,
            WEATHER;
            companion object {
                fun getStringArray(): List<String> {
                    return enumValues<PartialUpdate>().map { it.name }
                }
            }
        }
    }
}