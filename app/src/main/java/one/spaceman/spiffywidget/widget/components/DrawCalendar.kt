package one.spaceman.spiffywidget.widget.components

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import one.spaceman.spiffywidget.state.CalendarEvent
import one.spaceman.spiffywidget.ui.theme.Colors
import one.spaceman.spiffywidget.ui.theme.typography
import one.spaceman.spiffywidget.widget.SpiffyWidget.Companion.LARGE
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

@Composable
fun DrawCalendar(
    context: Context,
    events: List<CalendarEvent>
) {
    val timeZone = TimeZone.currentSystemDefault()
    val now = Clock.System.now()
    val today = now.toLocalDateTime(timeZone).date

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .cornerRadius(5.dp),
    ) {
        val numberDays = (LocalSize.current.width / 80).value.toInt()
        val week = (1..numberDays).map {
            today.plus(it, DateTimeUnit.DAY)
        }
        DrawDay(context, events, today, true)
        week.forEach { day ->
            key(day) {
                DrawDay(context, events, day)
            }
        }
    }
    LazyColumn(
        modifier = GlanceModifier.padding(bottom = 10.dp)
    ) {
        items(events) { e ->
            DrawEvent(context, e)
        }
    }
}

@Composable
internal fun RowScope.DrawDay(
    context: Context,
    events: List<CalendarEvent>,
    day: LocalDate,
    isToday: Boolean = false,
) {
    val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
    ContentUris.appendId(builder, day.toEpochDays().days.inWholeMilliseconds)
    val intent = Intent(Intent.ACTION_VIEW).setData(builder.build()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    Column(
        modifier = if (isToday) {
            GlanceModifier.padding(5.dp).cornerRadius(10.dp).background(Colors.background).clickable { context.startActivity(intent) }
        } else {
            GlanceModifier.defaultWeight().padding(vertical = 5.dp).cornerRadius(10.dp).clickable { context.startActivity(intent) }
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.Top,
    ) {

        Text(
            text = if (isToday) day.dayOfWeek.name else day.format(LocalDate.Format { dayOfWeek(DayOfWeekNames.ENGLISH_ABBREVIATED) }).take(2),
            style = GlanceTheme.typography.small,
            maxLines = 1,
        )
        Text(
            modifier = GlanceModifier.padding(vertical = (-5).dp),
            text = if (isToday) "${day.month.name.take(3)} ${day.day}" else "${day.day}",
            style = GlanceTheme.typography.largeBold,
        )
        Row(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            events.forEach { e ->
                if (e.onDays.contains(day.day))
                    Text(
                        text = "●",
                        style = GlanceTheme.typography.extraSmall.copy(color = ColorProvider(Color(e.color), Color(e.color))),
                    )
            }
        }
    }
}

@Composable
internal fun DrawEvent(
    context: Context,
    event: CalendarEvent,
) {
    val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.id)
    val intent = Intent(Intent.ACTION_VIEW).setData(uri).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    Row(
        modifier = GlanceModifier
            .padding(vertical = 3.dp)
            .fillMaxWidth()
            .clickable { context.startActivity(intent) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (LocalSize.current.width >= LARGE.height) {
            Spacer(
                modifier = GlanceModifier
                    .height(GlanceTheme.typography.smaller.fontSize!!.value.dp)
                    .width(5.dp)
                    .cornerRadius(3.dp)
                    .background(Color(event.color)),
            )
            Text(
                text = event.title,
                modifier = GlanceModifier.defaultWeight().padding(start = 10.dp),
                maxLines = 1,
                style = GlanceTheme.typography.smaller.copy(textAlign = TextAlign.Start),
            )
            Text(
                text = event.dateString,
                maxLines = 1,
                style = GlanceTheme.typography.small.copy(textAlign = TextAlign.End),
            )
        } else {
            Spacer(
                modifier = GlanceModifier
                    .height(GlanceTheme.typography.smaller.fontSize!!.value.dp.times(2))
                    .width(5.dp)
                    .cornerRadius(3.dp)
                    .background(Color(event.color)),
            )
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.title,
                    modifier = GlanceModifier.padding(start = 10.dp),
                    maxLines = 1,
                    style = GlanceTheme.typography.smaller.copy(textAlign = TextAlign.Start),
                )
                Text(
                    text = event.dateString,
                    maxLines = 1,
                    style = GlanceTheme.typography.small,
                    modifier = GlanceModifier.padding(start = 15.dp)
                )
            }
        }
    }
}

