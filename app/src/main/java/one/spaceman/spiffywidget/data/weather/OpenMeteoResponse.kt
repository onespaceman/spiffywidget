package one.spaceman.spiffywidget.data.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenMeteoResponse(
    @SerialName("hourly")
    val hourly: OpenMeteoHourly,
    @SerialName("daily")
    val daily: OpenMeteoDaily,
    @SerialName("current")
    val current: OpenMeteoCurrent
)

@Serializable
data class OpenMeteoHourly(
    @SerialName("time")
    val epochSeconds: List<Long>,
    @SerialName("weather_code")
    val weatherCode: List<Int>,
    @SerialName("temperature_2m")
    val temperature: List<Double>,
    @SerialName("precipitation")
    val precipitation: List<Double>,
    @SerialName("is_day")
    val isDay: List<Int>,
)

@Serializable
data class OpenMeteoDaily(
    @SerialName("time")
    val dayStartEpochSeconds: List<Long>,
    @SerialName("weather_code")
    val weatherCode: List<Int>,
    @SerialName("temperature_2m_max")
    val temperatureMax: List<Double>,
    @SerialName("temperature_2m_min")
    val temperatureMin: List<Double>,
    @SerialName("sunrise")
    val sunriseEpochSeconds: List<Long>,
    @SerialName("sunset")
    val sunsetEpochSeconds: List<Long>,
    @SerialName("precipitation_sum")
    val precipitation: List<Double>,
)

@Serializable
data class OpenMeteoCurrent(
    @SerialName("is_day")
    val isDay: Int
)