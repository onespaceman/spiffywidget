package one.spaceman.spiffywidget.state

import kotlinx.serialization.Serializable
import one.spaceman.spiffywidget.data.weather.WeatherCodes

@Serializable
data class SpiffyWidgetState(
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
    val start: Long,
    val end: Long,
)

@Serializable
data class Weather(
    val lastUpdate: Long,
    val temperature: Int,
    val temperatureLow: Int,
    val temperatureHigh: Int,
    val uvIndex: Int,
    val code: WeatherCodes,
    val extra: String,
    val location: String = "",
)

@Serializable
data class Configuration(
    val homeTimeZone: String? = null,
    val weatherApp: String? = null,
)