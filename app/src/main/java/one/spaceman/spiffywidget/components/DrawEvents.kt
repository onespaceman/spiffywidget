package one.spaceman.spiffywidget.components

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
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
    context: Context, events: List<CalendarEvent>?
) {
    if (events.isNullOrEmpty()) return
    val style = textStyle.copy(color = GlanceTheme.colors.secondary)

    events.forEach {
        val builder: Uri.Builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
        ContentUris.appendId(builder, it.id)
        val intent = Intent(Intent.ACTION_VIEW).setData(builder.build())
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        Row(
            modifier = GlanceModifier.padding(bottom = 3.dp).clickable {
                context.startActivity(intent)
            }) {
            Text(
                text = it.date, maxLines = 1, style = style
            )
            Text(
                text = " ⋄ ",
                modifier = GlanceModifier.padding(horizontal = 5.dp),
                style = style.copy(color = GlanceTheme.colors.tertiary)
            )
            Text(
                text = it.title, maxLines = 1, style = style
            )
        }
    }
}