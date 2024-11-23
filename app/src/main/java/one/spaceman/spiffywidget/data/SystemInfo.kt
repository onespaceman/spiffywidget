package one.spaceman.spiffywidget.data

import android.content.res.Resources
import java.time.Instant
import java.time.LocalDateTime
import java.util.Locale
import java.util.TimeZone

data class SystemInfo(
    val now: Instant = Instant.now(),
    val date: LocalDateTime = LocalDateTime.now(),
    val timeZone: TimeZone = TimeZone.getDefault(),
    val locale: Locale = Resources.getSystem().configuration.locales[0]
)