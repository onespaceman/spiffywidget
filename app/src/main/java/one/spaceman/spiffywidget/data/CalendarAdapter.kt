package one.spaceman.spiffywidget.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.app.ActivityCompat
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import one.spaceman.spiffywidget.state.CalendarEvent
import one.spaceman.spiffywidget.theme.formatTime
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.collections.mutableListOf

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
    val start: Instant,
    val end: Instant,
    val allDay: Boolean?,
    val displayColor: Int?,
)

object CalendarAdapter {
    private fun getEvents(
        context: Context, now: Instant
    ): Set<EventItem> {
        val events: MutableSet<EventItem> = HashSet()
        val uri = CalendarContract.Events.CONTENT_URI
        val from = now.toEpochMilli()
        val to = now.plus(7, ChronoUnit.DAYS).toEpochMilli()

        try {
            val cur = context.contentResolver.query(
                uri,
                EVENT_PROJECTION,
                "(${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} <= ?) OR (${CalendarContract.Events.DTSTART} <= ? AND ${CalendarContract.Events.DTEND} >= ?)",
                arrayOf(from.toString(), to.toString(), from.toString(), from.toString()),
                null
            )

            while (cur?.moveToNext() == true) {
                val start = cur.getLongOrNull(4)
                val end = cur.getLongOrNull(5)
                val allDay = cur.getIntOrNull(6) == 1
                val visible = cur.getIntOrNull(8) == 1

                if (visible && start != null && end != null) {
                    events.add(
                        EventItem(
                            id = cur.getLong(0),
                            title = cur.getStringOrNull(1),
                            eventLocation = cur.getStringOrNull(2),
                            status = cur.getIntOrNull(3),
                            start = Instant.ofEpochMilli(start),
                            end = Instant.ofEpochMilli(end),
                            allDay = allDay,
                            displayColor = cur.getIntOrNull(7),
                        )
                    )
                }
            }
            cur?.close()

            return events.toSortedSet(compareBy { it.start })
        } catch (_: Exception) {
            return events
        }
    }

    // Format event date for display
    private fun getDateString(event: EventItem): String {
        val now = Instant.now()

        return if (event.allDay == true) {
            // Format all day events
            if (now > event.start) {
                "Today"
            } else if (now > event.start.plus(1, ChronoUnit.DAYS)) {
                "Tomorrow"
            } else {
                DateTimeFormatter.ofPattern("MMM d").withZone(ZoneId.systemDefault()).format(event.start)
            }
        } else {
            // Format regular events
            if (now > event.start) {
                "Now"
            } else if (now.plus(1, ChronoUnit.DAYS) > event.start) {
                DateTimeFormatter.ofPattern("h:mma").withZone(ZoneId.systemDefault()).format(event.start)
            } else if (now.plus(2, ChronoUnit.DAYS) > event.start) {
                formatTime(DateTimeFormatter.ofPattern("'Tomorrow at' h:mm a").withZone(ZoneId.systemDefault()).format(event.start))
            } else {
                formatTime(DateTimeFormatter.ofPattern("MMM d 'at' h:mma").withZone(ZoneId.systemDefault()).format(event.start))
            }
        }
    }

    fun get(context: Context): List<CalendarEvent> {
        val widgetEvents = mutableListOf<CalendarEvent>()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALENDAR,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val events = getEvents(context, Instant.now())
            events.forEach {
                widgetEvents.add(
                    CalendarEvent(
                        id = it.id,
                        title = it.title.toString(),
                        start = it.start.toEpochMilli(),
                        end = it.end.toEpochMilli(),
                        dateString = getDateString(it),
                        color = it.displayColor,
                    )
                )
            }
        }

        return widgetEvents.toList()
    }
}