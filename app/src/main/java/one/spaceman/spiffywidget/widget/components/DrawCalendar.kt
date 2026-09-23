package one.spaceman.spiffywidget.widget.components

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.background
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
import androidx.glance.unit.ColorProvider
import one.spaceman.spiffywidget.state.CalendarEvent
import one.spaceman.spiffywidget.ui.theme.GlanceTypography
import one.spaceman.spiffywidget.ui.theme.formatTime
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@SuppressLint("RestrictedApi")
@Composable
fun DrawCalendar(
    context: Context,
    style: GlanceTypography,
    events: List<CalendarEvent>,
    alarm: String?,
) {
    // Week View
    val date = ZonedDateTime.now()

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
            // Draw each day
            val dayOfWeekToday by remember() { mutableStateOf(date.dayOfWeek) }
            val modifier = GlanceModifier.cornerRadius(10.dp).padding(vertical = 5.dp).defaultWeight()

            val week = Array(7) { date }
            (0L..6L).forEach {
                val day = date.plusDays(it)
                week[day.dayOfWeek.value - 1] = day
            }

            week.forEach { day ->
                // style current day
                val (modifier, style) = if (day.dayOfWeek == dayOfWeekToday) {
                    modifier.background(GlanceTheme.colors.secondary) to style
                    // style days in next week
                } else if (day.dayOfWeek < dayOfWeekToday) {
                    modifier to style.copy(color = ColorProvider(style.color.getColor(context).copy(alpha = 0.5f)))
                    // default style
                } else {
                    modifier to style
                }

                // Click action
                val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
                ContentUris.appendId(builder, day.toEpochSecond() * 1000)
                val intent = Intent(Intent.ACTION_VIEW).setData(builder.build()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                Column(
                    modifier = modifier.clickable { context.startActivity(intent) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        modifier = GlanceModifier,
                        text = day.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()).substring(0, 2),
                        style = style.regularType,
                        maxLines = 1,
                    )
                    Text(
                        modifier = GlanceModifier.padding(vertical = (-5).dp),
                        text = "${day.dayOfMonth}",
                        style = style.copy(fontWeight = FontWeight.Bold).largeType,
                    )
                    Row(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "",
                            style = style.extraSmallType,
                        )
                        events.forEach { e ->
                            if (isOnDay(day, e)) {
                                Text(
                                    text = "●",
                                    style = style.copy(color = ColorProvider(Color(e.color))).extraSmallType,
                                )
                            }
                        }
                    }
                }
            }
        }
        LazyColumn {
            items(events) { e ->
                val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, e.id)
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
                                .height(style.regular.dp)
                                .width(5.dp)
                                .cornerRadius(3.dp)
                                .background(Color(e.color))
                                .padding(all = 10.dp),
                        )
                        Text(
                            text = e.title,
                            modifier = GlanceModifier.padding(start = 10.dp),
                            maxLines = 1,
                            style = style.copy(textAlign = TextAlign.Start).regularType
                        )
                    }
                    Text(
                        text = dateString(e),
                        modifier = GlanceModifier,
                        maxLines = 1,
                        style = style.copy(textAlign = TextAlign.End).regularType
                    )
                }
            }
        }
    }
}

// Check if an event is on a certain day
fun isOnDay(day: ZonedDateTime, event: CalendarEvent): Boolean {
    val zone = if (event.allDay) {
        ZoneId.of("UTC")
    } else {
        ZoneId.systemDefault()
    }

    val start = ZonedDateTime.ofInstant(Instant.ofEpochMilli(event.start + 1), zone)
    val end = ZonedDateTime.ofInstant(Instant.ofEpochMilli(event.end - 1), zone)

    return day.dayOfYear in start.dayOfYear..end.dayOfYear
}

// Human-readable date string
fun dateString(event: CalendarEvent): String {
    val now = Instant.now()
    val start = Instant.ofEpochMilli(event.start)

    return if (event.allDay) {
        // Format all day events
        if (now > start) {
            "Today"
        } else if (now > start.plus(1, ChronoUnit.DAYS)) {
            "Tomorrow"
        } else {
            DateTimeFormatter.ofPattern("MMM d").withZone(ZoneId.of("UTC")).format(start)
        }
    } else {
        // Format regular events
        if (now > start) {
            "Now"
        } else if (now.plus(1, ChronoUnit.DAYS) > start) {
            DateTimeFormatter.ofPattern("h:mma").withZone(ZoneId.systemDefault()).format(start)
        } else if (now.plus(2, ChronoUnit.DAYS) > start) {
            formatTime(DateTimeFormatter.ofPattern("'Tomorrow at' h:mm a").withZone(ZoneId.systemDefault()).format(start))
        } else {
            formatTime(DateTimeFormatter.ofPattern("MMM d 'at' h:mma").withZone(ZoneId.systemDefault()).format(start))
        }
    }
}