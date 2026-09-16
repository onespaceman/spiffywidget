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
import java.time.Instant
import java.time.temporal.ChronoUnit

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
    val start: Long,
    val end: Long,
    val allDay: Boolean,
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
                            start = start,
                            end = end,
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
                        allDay = it.allDay,
                        start = it.start,
                        end = it.end,
                        color = it.displayColor,
                    )
                )
            }
        }

        return widgetEvents.toList()
    }
}