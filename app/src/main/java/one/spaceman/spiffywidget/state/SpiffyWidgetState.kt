package one.spaceman.spiffywidget.state

import kotlinx.serialization.Serializable

@Serializable
data class SpiffyWidgetState(
    val alarm: String? = null,
    val events: List<CalendarEvent>? = emptyList(),
    val weather: Weather? = null,
)

@Serializable
data class CalendarEvent(
    val id: Long,
    val title: String,
    val date: String,
)

@Serializable
data class Weather(
    val lastUpdate: Long,
    val temperature: Int,
    val temperatureLow: Int,
    val temperatureHigh: Int,
    val uvIndex: Int,
    val description: String,
    val extra: String,
    val location: String = "",
)