package one.spaceman.spiffywidget.components

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import one.spaceman.spiffywidget.state.CalendarEvent
import one.spaceman.spiffywidget.theme.editColor
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

@SuppressLint("RestrictedApi")
@Composable
fun DrawCalendar(
    context: Context,
    events: List<CalendarEvent>?
) {
    val style = TextStyle(
        color = GlanceTheme.colors.onPrimaryContainer,
        fontSize = 18.sp,
    )

    // Week View
    val date = LocalDateTime.now()

    Column {
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
            val style = style.copy(color = GlanceTheme.colors.primary)

            val week = Array(7) { date }
            (0L..6L).forEach {
                val day = date.plusDays(it)
                week[day.dayOfWeek.value - 1] = day
            }

            week.forEach { day ->
                // style current day
                val (modifier, style) = if (day.dayOfWeek == dayOfWeekToday) {
                    modifier.background(GlanceTheme.colors.tertiary) to
                            style.copy(color = GlanceTheme.colors.onTertiary)
                    // style days in next week
                } else if (day.dayOfWeek < dayOfWeekToday) {
                    modifier to style.copy(color = editColor(GlanceTheme.colors.tertiary.getColor(context), alpha = 0.5f))
                    // default style
                } else modifier to style

                // Click action
                val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
                ContentUris.appendId(builder, day.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
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
                            fontSize = style.fontSize?.times(0.65),
                        ),
                        maxLines = 1,
                    )
                    Text(
                        modifier = GlanceModifier.padding(vertical = (-5).dp),
                        text = "${day.dayOfMonth}",
                        style = style.copy(
                            fontSize = style.fontSize?.times(1.5),
                            fontWeight = FontWeight.Medium
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

    }

    events?.forEach {
        val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, it.id)
        val intent = Intent(Intent.ACTION_VIEW).setData(uri).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        Row(
            modifier = GlanceModifier
                .padding(horizontal = 10.dp, vertical = 3.dp)
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
                modifier = GlanceModifier.defaultWeight()
            ) {
                Text(
                    text = "⋄ ",
                    modifier = GlanceModifier.padding(horizontal = 5.dp),
                    style = style.copy(color = color)
                )
                Text(
                    text = it.title,
                    modifier = GlanceModifier,
                    maxLines = 1,
                    style = style.copy(
                        textAlign = TextAlign.Start
                    )
                )
            }
            Text(
                text = it.dateString,
                modifier = GlanceModifier,
                maxLines = 1,
                style = style.copy(
                    fontSize = style.fontSize?.times(0.6),
                    textAlign = TextAlign.End,
                )
            )
        }
    }
}

fun isOnDay(day: LocalDateTime, event: CalendarEvent): Boolean {
    val zone = ZoneId.systemDefault()

    val start = Instant.ofEpochMilli(event.start + 1).atZone(zone).toLocalDateTime()
    val end = Instant.ofEpochMilli(event.end - 1).atZone(zone).toLocalDateTime()
    return day.dayOfYear in start.dayOfYear..end.dayOfYear
}