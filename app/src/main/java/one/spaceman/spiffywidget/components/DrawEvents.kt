package one.spaceman.spiffywidget.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import one.spaceman.spiffywidget.state.CalendarEvent
import one.spaceman.spiffywidget.theme.DrawSpacer
import one.spaceman.spiffywidget.theme.itemModifier
import one.spaceman.spiffywidget.theme.textStyle

// Show the next few events within the next week
@Composable
fun DrawEvents(
    context: Context,
    events: List<CalendarEvent>?
) {
    if (events.isNullOrEmpty()) return

    events.forEach {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("content://com.android.calendar/time/${it.id}")
        )
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = itemModifier.clickable {
                context.startActivity(intent)
            }
        ) {
            Text(
                text = it.date,
                maxLines = 1,
                style = textStyle
            )
            Text(
                text = " ⋄ ",
                modifier = GlanceModifier.padding(horizontal = 5.dp),
                style = textStyle.copy(
                    color = ColorProvider(Color(0xFF89DCEB))
                )
            )
            Text(
                text = it.title,
                maxLines = 1,
                style = textStyle
            )
        }
        DrawSpacer()
    }
}