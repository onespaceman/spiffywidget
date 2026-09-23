package one.spaceman.spiffywidget.data

import android.app.AlarmManager
import android.content.Context
import one.spaceman.spiffywidget.ui.theme.formatTime
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object AlarmAdapter {
    fun get(context: Context): String? {
        val alarmsList = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val nextAlarm = alarmsList.runCatching { nextAlarmClock }.getOrNull()
        if (nextAlarm != null) {
            val instant = Instant.ofEpochMilli(nextAlarm.triggerTime)
            if (Instant.now().plus(1L, ChronoUnit.DAYS) > instant) {
                val time = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
                val alarmString = formatTime(
                    DateTimeFormatter.ofPattern("h:mma").format(time)
                )
                return alarmString.lowercase()
            }
        }
        return null
    }
}