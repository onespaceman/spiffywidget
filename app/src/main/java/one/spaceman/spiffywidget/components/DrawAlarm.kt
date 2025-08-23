package one.spaceman.spiffywidget.components

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.theme.textStyle

// Show the next scheduled alarm
@Composable
fun DrawAlarm(
    context: Context,
    alarm: String?
) {
    if (alarm.isNullOrEmpty()) return

    Row(
        modifier = GlanceModifier.padding(bottom = 3.dp).clickable {
            context.startActivity(
                Intent(AlarmClock.ACTION_SHOW_ALARMS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = GlanceModifier
                .padding(end = 7.dp)
                .size(24.dp),
            provider = ImageProvider(R.drawable.baseline_alarm_24),
            colorFilter = ColorFilter.tint(GlanceTheme.colors.tertiary),
            contentDescription = "Alarm",
            contentScale = ContentScale.Fit
        )
        Text(
            text = alarm,
            style = textStyle.copy(
                color = GlanceTheme.colors.secondary
            )
        )
    }
}
