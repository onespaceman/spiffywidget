package one.spaceman.spiffywidget.components

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.ButtonDefaults.buttonColors
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.appwidget.components.FilledButton
import androidx.glance.layout.padding
import androidx.glance.unit.ColorProvider
import one.spaceman.spiffywidget.R

// Show the next scheduled alarm
@Composable
fun DrawAlarm(
    context: Context,
    alarm: String?
) {
    if (alarm.isNullOrEmpty()) return
    FilledButton(
        text = alarm.toString(),
        modifier = GlanceModifier.padding(0.dp, 5.dp),
        colors = buttonColors(
            backgroundColor = ColorProvider(Color(0xFFF38BA8)), // Red
        ),
        icon = ImageProvider(R.drawable.baseline_alarm_24),
        onClick = {
            context.startActivity(
                Intent(AlarmClock.ACTION_SHOW_ALARMS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    )
}
