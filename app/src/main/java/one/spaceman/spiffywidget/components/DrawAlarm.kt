package one.spaceman.spiffywidget.components

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.theme.DrawSpacer
import one.spaceman.spiffywidget.theme.itemModifier
import one.spaceman.spiffywidget.theme.textStyle

// Show the next scheduled alarm
@Composable
fun DrawAlarm(
    context: Context,
    alarm: String?
) {
    if (alarm.isNullOrEmpty()) return

    Row(
        modifier = itemModifier.background(R.color.maroon),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = GlanceModifier
                .padding(end = 10.dp)
                .size(28.dp),
            provider = ImageProvider(R.drawable.baseline_alarm_24),
            contentDescription = "Alarm",
            contentScale = ContentScale.Fit
        )
        Text(
            text = alarm,
            modifier = GlanceModifier.clickable {
                context.startActivity(
                    Intent(AlarmClock.ACTION_SHOW_ALARMS)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            },
            maxLines = 1,
            style = textStyle
        )
    }
    DrawSpacer()
}
