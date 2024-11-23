package one.spaceman.spiffywidget.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.app.ActivityCompat
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.state.CalendarEvent
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.SortedSet


internal val EVENT_PROJECTION = arrayOf(
    CalendarContract.Events._ID,
    CalendarContract.Events.TITLE,
    CalendarContract.Events.EVENT_LOCATION,
    CalendarContract.Events.STATUS,
    CalendarContract.Events.DTSTART,
    CalendarContract.Events.DTEND,
    CalendarContract.Events.ALL_DAY,
    CalendarContract.Events.DISPLAY_COLOR,
    CalendarContract.Events.VISIBLE,
)

internal data class EventItem(
    val id: Long,
    val title: String?,
    val eventLocation: String?,
    val status: Int?,
    val dtStart: Instant?,
    val dtEnd: Instant?,
    val allDay: Boolean?,
    val displayColor: Int?,
)

object CalendarAdapter {
    private fun getEvents(
        context: Context,
        now: Instant
    ): Set<EventItem> {
        val events: MutableSet<EventItem> = HashSet()
        val uri = CalendarContract.Events.CONTENT_URI
        val begin = now.toEpochMilli()
        val end = begin.plus(604800000) // 1 Week

        val cur = context.contentResolver.query(
            uri,
            EVENT_PROJECTION,
            "(${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} <= ?) OR (${CalendarContract.Events.DTSTART} <= ? AND ${CalendarContract.Events.DTEND} >= ?)",
            arrayOf(begin.toString(), end.toString(), begin.toString(), begin.toString()),
            null
        )

        while (cur?.moveToNext() == true) {
            val dtStart = cur.getLongOrNull(4)
            val dtEnd = cur.getLongOrNull(5)
            val allDay = cur.getIntOrNull(6) == 1
            val visible = cur.getIntOrNull(8) == 1

            if (visible) {
                events.add(
                    EventItem(
                        id = cur.getLong(0),
                        title = cur.getStringOrNull(1),
                        eventLocation = cur.getStringOrNull(2),
                        status = cur.getIntOrNull(3),
                        dtStart = Instant.ofEpochMilli(dtStart!!),
                        dtEnd = Instant.ofEpochMilli(dtEnd!!),
                        allDay = allDay,
                        displayColor = cur.getIntOrNull(7),
                    )
                )
            }
        }
        cur?.close()

        return filterEvents(events.toSortedSet(compareBy { it.dtStart }), now)
    }

    private fun filterEvents(
        events: SortedSet<EventItem>,
        now: Instant
    ): SortedSet<EventItem> {
        if (events.isNotEmpty()) {
            val firstEventDay = if (events.first().dtStart!! >= now) {
                events.first().dtStart!!.truncatedTo(ChronoUnit.DAYS)
            } else {
                now.truncatedTo(ChronoUnit.DAYS)
            }

            events.removeIf { it.dtStart!! >= firstEventDay.plus(48L, ChronoUnit.HOURS) }
        }
        return events
    }

    private fun formatEvent(
        event: EventItem,
        info: SystemInfo
    ): String {
        var dateString = ""
        val localNow = ZonedDateTime.ofInstant(info.now, info.timeZone.toZoneId())
        val start = ZonedDateTime.ofInstant(event.dtStart, info.timeZone.toZoneId())
        // val end = ZonedDateTime.ofInstant(event.dtEnd, info.timeZone.id)

        dateString += if (start <= localNow) {
            "Now"
        } else if (localNow.truncatedTo(ChronoUnit.DAYS) == start.truncatedTo(ChronoUnit.DAYS)) {
            DateTimeFormatter.ofPattern("h:mm a").format(start)
        } else if (localNow.truncatedTo(ChronoUnit.DAYS).plusDays(1L) == start.truncatedTo(
                ChronoUnit.DAYS
            )
        ) {
            "Tomorrow at " + DateTimeFormatter.ofPattern("h:mm a").format(start)
        } else {
            DateTimeFormatter.ofPattern("MMM d").format(start) + " at " +
                    DateTimeFormatter.ofPattern("h:mm a").format(start)
        }

        return dateString
    }

    private fun formatAllDay(
        event: EventItem,
        info: SystemInfo
    ): String {
        var dateString = ""
        val localNow = LocalDateTime.ofInstant(info.now, info.timeZone.toZoneId()).toLocalDate()
        val start = LocalDateTime.ofInstant(event.dtStart, ZoneId.of("Etc/UTC")).toLocalDate()
        // val end = LocalDateTime.ofInstant(event.dtStart, ZoneId.of("Etc/UTC")).toLocalDate()

        dateString += if (start <= localNow) {
            "Today"
        } else if (start <= localNow.plusDays(1L)) {
            "Tomorrow"
        } else {
            DateTimeFormatter.ofPattern("MMM d").format(start)
        }
        return dateString
    }

    fun get(
        context: Context,
        info: SystemInfo,
    ): List<CalendarEvent> {
        val widgetEvents = mutableListOf<CalendarEvent>()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALENDAR,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val events = getEvents(context, info.now)
            events.forEach {
                val dateString = if (it.allDay == true) {
                    formatAllDay(it, info)
                } else {
                    formatEvent(it, info)
                }

                widgetEvents.add(
                    CalendarEvent(
                        id = it.id,
                        title = it.title.toString(),
                        date = dateString,
                        color = /* it.displayColor ?: */ R.color.sky
                    )
                )
            }
        }

        return widgetEvents.toList()
    }
}