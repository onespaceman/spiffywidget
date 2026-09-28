package one.spaceman.spiffywidget.state

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import one.spaceman.spiffywidget.data.weather.WeatherCodes
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class SpiffyWidgetState(
    val lastUpdate: Instant = Clock.System.now(),
    val alarm: String? = null,
    val events: List<CalendarEvent> = emptyList(),
    val weather: Weather? = null,
    val settings: Configuration = Configuration(),
)

@Serializable
data class CalendarEvent(
    val id: Long,
    val title: String,
    val allDay: Boolean,
    val color: Int,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val onDays: List<Int>,
    val dateString: String,
)

@Serializable
data class Weather(
    val lastUpdate: Instant = Clock.System.now(),
    val temperature: Int,
    val temperatureLow: Int,
    val temperatureHigh: Int,
    val uvIndex: Int,
    val code: WeatherCodes,
    val extra: String,
    val location: String? = null,
)

@Serializable
data class Configuration(
    val homeTimeZone: String? = null,
    val weatherApp: String? = null,
    val invertColors: Boolean = false,
)