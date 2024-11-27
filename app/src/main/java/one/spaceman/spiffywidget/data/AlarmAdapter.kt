package one.spaceman.spiffywidget.data

import android.app.AlarmManager
import android.content.Context
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object AlarmAdapter {
    fun get(
        context: Context,
        info: SystemInfo
    ): String? {
        val alarmsList = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val nextAlarm = alarmsList.runCatching { nextAlarmClock }.getOrNull()
        if (nextAlarm != null) {
            val instant = Instant.ofEpochMilli(nextAlarm.triggerTime)
            if (info.now.plus(1L, ChronoUnit.DAYS) > instant) {
                val time = ZonedDateTime.ofInstant(instant, info.timeZone.toZoneId())
                val alarmString = DateTimeFormatter.ofPattern("h:mma").format(time)
                return alarmString.lowercase()
            }
        }
        return null
    }
}