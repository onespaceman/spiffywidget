package one.spaceman.spiffywidget.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import one.spaceman.spiffywidget.worker.WidgetWorkManager.Companion.PartialUpdate

class BroadcastReceiverManager {
    private var isRegistered: Boolean = false
    private val intentFilter = IntentFilter(
        android.app.AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED,
    )

    fun register(context: Context) {
        try {
            if (!isRegistered) {
                context.registerReceiver(
                    WidgetBroadcastReceiver(),
                    intentFilter,
                    Context.RECEIVER_EXPORTED
                )
            }
        } finally {
            isRegistered = true
        }
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(WidgetBroadcastReceiver())
        isRegistered = false
    }
}

class WidgetBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        when (intent?.action) {
            android.app.AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED -> {
                if (context != null) {
                    WidgetWorkManager(context).updateNow(PartialUpdate.ALARM)
                }
            }
            Intent.ACTION_LOCALE_CHANGED -> {
                if (context != null) {
                    WidgetWorkManager(context).updateNow()
                }
            }
        }
    }
}