package one.spaceman.spiffywidget.widget.components

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
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.ui.theme.GlanceTypography

@Composable
fun DrawAlarm(
    context: Context,
    style: GlanceTypography,
    alarm: String?,
) {
    if (!alarm.isNullOrEmpty()) {
        val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        Column(
            modifier = GlanceModifier.padding(vertical = 5.dp)
        ) {
            Row(
                modifier = GlanceModifier
                    .padding(5.dp, 3.dp)
                    .cornerRadius(25.dp)
                    .background(GlanceTheme.colors.secondary)
                    .clickable { context.startActivity(intent) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    provider = ImageProvider(R.drawable.alarm_24px),
                    contentDescription = "Alarm Icon",
                    modifier = GlanceModifier.size(style.regular.dp),
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onSecondary)
                )
                Text(
                    text = alarm,
                    style = style.copy(color = GlanceTheme.colors.onSecondary).regularType
                )
            }
        }
    }
}