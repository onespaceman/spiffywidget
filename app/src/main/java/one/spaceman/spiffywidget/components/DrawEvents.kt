package one.spaceman.spiffywidget.components

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.layout.Row
import androidx.glance.layout.padding
import androidx.glance.text.Text
import one.spaceman.spiffywidget.state.CalendarEvent
import one.spaceman.spiffywidget.theme.textStyle

// Show the next few events within the next week
@Composable
fun DrawEvents(
    context: Context,
    events: List<CalendarEvent>?
) {
    if (events.isNullOrEmpty()) return
    val style = textStyle.copy(color = GlanceTheme.colors.onPrimary)

    events.forEach {
        val intent = Intent(
            Intent.ACTION_VIEW,
            "content://com.android.calendar/time/${it.id}".toUri()
        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        Row (
            modifier = GlanceModifier.padding(bottom = 3.dp).clickable {
                context.startActivity(intent)
            }
        ) {
            Text(
                text = it.date,
                maxLines = 1,
                style = style
            )
            Text(
                text = " ⋄ ",
                modifier = GlanceModifier.padding(horizontal = 5.dp),
                style = style.copy(
                    color = GlanceTheme.colors.inversePrimary
                )
            )
            Text(
                text = it.title,
                maxLines = 1,
                style = style
            )
        }
    }
}