package one.spaceman.spiffywidget.data

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract.Instances
import androidx.core.app.ActivityCompat
import one.spaceman.spiffywidget.state.CalendarEvent
import java.time.Instant
import java.time.temporal.ChronoUnit

internal val INSTANCE_PROJECTION = arrayOf(
    Instances.EVENT_ID, // 0
    Instances.TITLE, // 1
    Instances.DISPLAY_COLOR, // 2
    Instances.ALL_DAY, // 3
    Instances.BEGIN, // 4
    Instances.END, // 5
)

object CalendarAdapter {
    private fun getEvents(
        context: Context,
        now: Instant
    ): List<CalendarEvent> {
        val events = mutableListOf<CalendarEvent>()
        val from = now.toEpochMilli()
        val to = now.plus(7, ChronoUnit.DAYS).toEpochMilli()

        val uri = Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(uri, from) // from date
        ContentUris.appendId(uri, to) // to date

        try {
            val cur = context.contentResolver.query(
                uri.build(),
                INSTANCE_PROJECTION,
                null,
                null
            )

            while (cur?.moveToNext() == true) {
                events.add(
                    CalendarEvent(
                        id = cur.getLong(0),
                        title = cur.getString(1),
                        color = cur.getInt(2),
                        allDay = cur.getInt(3) == 1,
                        start = cur.getLong(4),
                        end = cur.getLong(5),
                    )
                )
            }
            cur?.close()
            return events
        } catch (_: Exception) {
            return events
        }
    }

    fun get(context: Context): List<CalendarEvent> {
        return if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALENDAR,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getEvents(context, Instant.now())
        } else {
            listOf()
        }
    }
}