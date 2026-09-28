package one.spaceman.spiffywidget.data

import android.app.AlarmManager
import android.content.Context
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

object AlarmAdapter {
    fun get(context: Context): String? {
        val alarmsList = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val nextAlarm = alarmsList.runCatching { nextAlarmClock }.getOrNull()
        if (nextAlarm != null) {
            val instant = Instant.fromEpochMilliseconds(nextAlarm.triggerTime)
            if (Clock.System.now().minus(instant) < 1.days) {
                return instant
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .format(LocalDateTime.Format {
                        amPmHour(Padding.NONE)
                        chars(":")
                        minute()
                        amPmMarker("ᴀᴍ", "ᴘᴍ")
                    })
            }
        }
        return null
    }
}