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
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
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
import one.spaceman.spiffywidget.ui.theme.default
import one.spaceman.spiffywidget.ui.theme.onDefault
import one.spaceman.spiffywidget.ui.theme.transparent
import one.spaceman.spiffywidget.ui.theme.typography
import one.spaceman.spiffywidget.ui.theme.withAlpha
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

@Composable
fun DrawCalendar(
    context: Context,
    events: List<CalendarEvent>
) {
    // Week View
    val timeZone = TimeZone.currentSystemDefault()
    val now = Clock.System.now()
    val today = now.toLocalDateTime(timeZone).date

    Column(
        modifier = GlanceModifier.padding(vertical = 5.dp)
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .cornerRadius(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val week = (0..6).map{
                    today.plus(it, DateTimeUnit.DAY)
                }.sortedBy { it.dayOfWeek }

            val modifier = GlanceModifier.defaultWeight().padding(vertical = 5.dp).cornerRadius(10.dp)

            week.forEach { day ->
                key(day) {
                    DrawDay(context, events, modifier, today, day)
                }
            }
        }
    }
    LazyColumn {
        items(events) { e ->
            DrawEvent(context, e)
        }
    }
}

@Composable
fun DrawDay(
    context: Context,
    events: List<CalendarEvent>,
    modifier: GlanceModifier,
    today: LocalDate,
    day: LocalDate,
) {
    val (bgColor, style) = when {
        day.dayOfWeek == today.dayOfWeek -> GlanceTheme.colors.default to
                GlanceTheme.typography.copy(color = GlanceTheme.colors.onDefault)
        day.dayOfWeek < today.dayOfWeek -> GlanceTheme.colors.transparent to
                GlanceTheme.typography.copy(color = GlanceTheme.colors.default.withAlpha(context, 0.6f))
        else -> GlanceTheme.colors.transparent to
                GlanceTheme.typography.copy(color = GlanceTheme.colors.default)
    }

    // Click action
    val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
    ContentUris.appendId(builder, day.toEpochDays().days.inWholeMilliseconds)
    val intent = Intent(Intent.ACTION_VIEW).setData(builder.build()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    Column(
        modifier = modifier.background(bgColor).clickable { context.startActivity(intent) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            modifier = GlanceModifier,
            text = day.format(LocalDate.Format { dayOfWeek(DayOfWeekNames.ENGLISH_ABBREVIATED) }).dropLast(1),
            style = style.small,
            maxLines = 1,
        )
        Text(
            modifier = GlanceModifier.padding(vertical = (-5).dp),
            text = "${day.day}",
            style = style.large.copy(fontWeight = FontWeight.Bold),
        )
        Row(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "",
                style = style.extraSmall,
            )
            events.forEach { e ->
                if (e.onDays.contains(day.day))
                    Text(
                        text = "●",
                        style = style.extraSmall.copy(color = ColorProvider(Color(e.color), Color(e.color))),
                    )
            }
        }
    }
}

@Composable
fun DrawEvent(
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
        verticalAlignment = Alignment.Bottom,
    ) {
        Row(
            modifier = GlanceModifier.defaultWeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = GlanceModifier
                    .height(GlanceTheme.typography.regular.fontSize!!.value.dp)
                    .width(5.dp)
                    .padding(all = 10.dp)
                    .cornerRadius(3.dp)
                    .background(Color(event.color)),
            )
            Text(
                text = event.title,
                modifier = GlanceModifier.padding(start = 10.dp),
                maxLines = 1,
                style = GlanceTheme.typography.regular.copy(textAlign = TextAlign.Start),
            )
        }
        Text(
            text = event.dateString,
            modifier = GlanceModifier,
            maxLines = 1,
            style = GlanceTheme.typography.small.copy(textAlign = TextAlign.End),
        )
    }
}