package one.spaceman.spiffywidget.state

import kotlinx.serialization.Serializable

@Serializable
data class SpiffyWidgetState (
    val alarm: String? = null,
    val location: String? = null,
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
    val temperatureLow: Int,
    val temperatureHigh: Int,
    val icon: Int,
    val iconDescription: String,
    val extra: String,
    val forecast: List<ForecastDay>,
)

@Serializable
data class ForecastDay(
    val time: String,
    val icon: Int,
    val iconDescription: String,
    val temperature: Int,
)