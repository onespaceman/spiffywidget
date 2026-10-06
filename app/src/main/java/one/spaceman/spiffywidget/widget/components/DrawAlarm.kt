package one.spaceman.spiffywidget.widget.components

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import androidx.compose.runtime.Composable
import androidx.glance.ImageProvider
import one.spaceman.spiffywidget.R

@Composable
fun DrawAlarm(
    context: Context,
    alarm: String?,
) {
    if (!alarm.isNullOrEmpty()) {
        val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        Pill(
            icon = ImageProvider(R.drawable.alarm_24px),
            text = alarm,
            onclick = { context.startActivity(intent) },
            content = {}
        )
    }
}