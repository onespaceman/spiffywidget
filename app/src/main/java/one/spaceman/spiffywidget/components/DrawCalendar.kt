package one.spaceman.spiffywidget.components

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.provider.CalendarContract
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.state.CalendarEvent
import one.spaceman.spiffywidget.theme.formatTime
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@SuppressLint("RestrictedApi")
@Composable
fun DrawCalendar(
    context: Context,
    events: List<CalendarEvent>?,
    alarm: String?,
    style: TextStyle,
) {

    // Week View
    val date = ZonedDateTime.now()

    Column(GlanceModifier.padding(horizontal = 10.dp)) {
        Row(
            modifier = GlanceModifier.padding(bottom = 5.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = GlanceModifier.defaultWeight(),
                text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d")).uppercase(),
                style = style.copy(fontSize = style.fontSize?.times(1.2)),
            )

            // Draw next alarm
            if (!alarm.isNullOrEmpty()) {
                Row(
                    modifier = GlanceModifier.defaultWeight().clickable {
                        context.startActivity(Intent(AlarmClock.ACTION_SHOW_ALARMS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    },
                    horizontalAlignment = Alignment.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        modifier = GlanceModifier.padding(end = 3.dp).size(style.fontSize!!.value.dp),
                        provider = ImageProvider(R.drawable.baseline_alarm_24),
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.tertiary),
                        contentDescription = "Alarm",
                        contentScale = ContentScale.Fit,
                    )
                    Text(
                        text = alarm,
                        style = style
                    )
                }
            }
        }

        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .cornerRadius(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Draw each day
            val dayOfWeekToday = date.dayOfWeek
            val modifier = GlanceModifier.cornerRadius(10.dp).padding(vertical = 5.dp).defaultWeight()

            val week = Array(7) { date }
            (0L..6L).forEach {
                val day = date.plusDays(it)
                week[day.dayOfWeek.value - 1] = day
            }

            week.forEach { day ->
                // style current day
                val (modifier, style) = if (day.dayOfWeek == dayOfWeekToday) {
                    modifier.background(GlanceTheme.colors.secondary) to
                            style.copy(color = GlanceTheme.colors.onSecondary)
                    // style days in next week
                } else if (day.dayOfWeek < dayOfWeekToday) {
                    modifier to style.copy(color = ColorProvider(style.color.getColor(context).copy(alpha = 0.5f)))
                    // default style
                } else modifier to style

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
                        style = style.copy(
                            fontSize = style.fontSize?.times(0.7),
                        ),
                        maxLines = 1,
                    )
                    Text(
                        modifier = GlanceModifier.padding(vertical = (-5).dp),
                        text = "${day.dayOfMonth}",
                        style = style.copy(
                            fontSize = style.fontSize?.times(1.5),
                            fontWeight = FontWeight.Bold
                        ),
                    )
                    Row(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "",
                            style = style.copy(
                                fontSize = style.fontSize?.times(0.4),
                            ),
                        )
                        events?.forEach { e ->
                            if (isOnDay(day, e)) {
                                val color = if (e.color != null) {
                                    ColorProvider(Color(e.color))
                                } else {
                                    style.color
                                }
                                Text(
                                    text = "●",
                                    style = style.copy(
                                        color = color,
                                        fontSize = style.fontSize?.times(0.4),
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }

        events?.forEach {
            val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, it.id)
            val intent = Intent(Intent.ACTION_VIEW).setData(uri).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            Row(
                modifier = GlanceModifier
                    .padding(vertical = 3.dp)
                    .fillMaxWidth()
                    .clickable { context.startActivity(intent) },
                verticalAlignment = Alignment.Bottom,
            ) {
                val color = if (it.color != null) {
                    ColorProvider(Color(it.color))
                } else {
                    style.color
                }
                Row(
                    modifier = GlanceModifier.defaultWeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(
                        modifier = GlanceModifier
                            .height(style.fontSize!!.value.dp)
                            .width(5.dp)
                            .cornerRadius(3.dp)
                            .background(color)
                            .padding(all = 10.dp),
                    )
                    Text(
                        text = it.title,
                        modifier = GlanceModifier.padding(start = 10.dp),
                        maxLines = 1,
                        style = style.copy(
                            textAlign = TextAlign.Start
                        )
                    )
                }
                Text(
                    text = dateString(it),
                    modifier = GlanceModifier,
                    maxLines = 1,
                    style = style.copy(
                        fontSize = style.fontSize?.times(0.7),
                        textAlign = TextAlign.End,
                    )
                )
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