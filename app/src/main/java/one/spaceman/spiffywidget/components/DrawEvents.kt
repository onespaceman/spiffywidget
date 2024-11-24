package one.spaceman.spiffywidget.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.text.Text
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.theme.DrawSpacer
import one.spaceman.spiffywidget.state.CalendarEvent
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
            Uri.parse("content://com.android.calendar/time/${it.id}"))
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        Text(
            text = "${it.title} ✧ ${it.date}",
            modifier = itemModifier
                .background(R.color.sky)
                .clickable {
                    context.startActivity(intent)
                },
            maxLines = 1,
            style = textStyle
        )
        DrawSpacer()
    }
}