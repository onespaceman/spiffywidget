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
//    val color: Int
)

@Serializable
data class Weather(
    val lastUpdate: Long,
    val outdated: Boolean = false,
    val temperature: Int,
    val temperatureLow: Int,
    val temperatureHigh: Int,
    val uvIndex: Int,
    val icon: List<Int>,
    val iconDescription: String,
    val extra: String,
    val forecast: List<ForecastDay>,
)

@Serializable
data class ForecastDay(
    val time: String,
    val icon: List<Int>,
    val iconDescription: String,
    val temperature: Int,
)