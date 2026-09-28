package one.spaceman.spiffywidget.data

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract.Instances
import androidx.core.app.ActivityCompat
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import one.spaceman.spiffywidget.state.CalendarEvent
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

internal val INSTANCE_PROJECTION = arrayOf(
    Instances.EVENT_ID, // 0
    Instances.TITLE, // 1
    Instances.DISPLAY_COLOR, // 2
    Instances.ALL_DAY, // 3
    Instances.BEGIN, // 4
    Instances.END, // 5
    Instances.EVENT_TIMEZONE, // 6
)

object CalendarAdapter {
    fun get(context: Context): List<CalendarEvent> {
        return if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALENDAR,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            readCalendar(context, Clock.System.now())
        } else {
            listOf()
        }
    }

    private fun readCalendar(
        context: Context,
        now: Instant
    ): List<CalendarEvent> {
        val events = mutableListOf<CalendarEvent>()
        val from = now.toEpochMilliseconds()
        val to = now.plus(7.days).toEpochMilliseconds()

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
                val allDay = cur.getInt(3) == 1
                val timeZone = TimeZone.of(cur.getString(6))
                val start = Instant.fromEpochMilliseconds(cur.getLong(4)).toLocalDateTime(timeZone)
                val end = Instant.fromEpochMilliseconds(cur.getLong(5) - 1).toLocalDateTime(timeZone)

                events.add(
                    CalendarEvent(
                        id = cur.getLong(0),
                        title = cur.getString(1),
                        color = cur.getInt(2),
                        allDay = allDay,
                        start = start,
                        end = end,
                        onDays = datesList(start, end),
                        dateString = dateString(
                            now.toLocalDateTime(TimeZone.currentSystemDefault()),
                            start,
                            end,
                            allDay,
                        ),
                    )
                )
            }
            cur?.close()
            return events.sortedBy { it.start }
        } catch (_: Exception) {
            return events.sortedBy { it.start }
        }
    }

    // Readable date for display
    fun dateString(
        now: LocalDateTime,
        start: LocalDateTime,
        end: LocalDateTime,
        allDay: Boolean
    ): String {
        return if (allDay) {
            when {
                now in start..end -> "Today"
                now.date.plus(1, DateTimeUnit.DAY) > start.date -> "Tomorrow"
                else -> start.format(LocalDateTime.Format {
                    monthName(MonthNames.ENGLISH_ABBREVIATED)
                    char(' ')
                    day(Padding.SPACE)
                })
            }
        } else {
            when {
                now > start -> "Now"
                now.date.plus(1, DateTimeUnit.DAY) > start.date -> {
                    start.format(LocalDateTime.Format {
                        amPmHour()
                        char(':')
                        minute()
                        amPmMarker("ᴀᴍ", "ᴘᴍ")
                    })
                }

                now.date.plus(2, DateTimeUnit.DAY) > start.date -> {
                    start.format(LocalDateTime.Format {
                        chars("Tomorrow • ")
                        amPmHour()
                        char(':')
                        minute()
                        amPmMarker("ᴀᴍ", "ᴘᴍ")
                    })
                }

                else -> {
                    start.format(LocalDateTime.Format {
                        monthName(MonthNames.ENGLISH_ABBREVIATED)
                        char(' ')
                        day(Padding.SPACE)
                        chars(" • ")
                        amPmHour()
                        char(':')
                        minute()
                        amPmMarker("ᴀᴍ", "ᴘᴍ")
                    })
                }
            }
        }
    }

    fun datesList(start: LocalDateTime, end: LocalDateTime): List<Int> {
        val start = start.date
        val end = end.date
        val dates = buildList {
            var date = start
            while (date <= end) {
                add(date)
                date = date.plus(1, DateTimeUnit.DAY)
            }
        }
        return dates.map { it.day }
    }
}