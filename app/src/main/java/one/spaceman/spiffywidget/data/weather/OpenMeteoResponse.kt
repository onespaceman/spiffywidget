package one.spaceman.spiffywidget.data.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenMeteoResponse(
    @SerialName("daily") val daily: OpenMeteoDaily,
    @SerialName("current") val current: OpenMeteoCurrent
)

@Serializable
data class OpenMeteoDaily(
    @SerialName("time") val dayStartEpochSeconds: List<Long>,
    @SerialName("weather_code") val weatherCode: List<Int>,
    @SerialName("temperature_2m_max") val temperatureMax: List<Double>,
    @SerialName("temperature_2m_min") val temperatureMin: List<Double>,
    @SerialName("sunrise") val sunriseEpochSeconds: List<Long>,
    @SerialName("sunset") val sunsetEpochSeconds: List<Long>,
    @SerialName("precipitation_sum") val precipitation: List<Double>,
    @SerialName("uv_index_max") val uvIndex: List<Double>,
)

@Serializable
data class OpenMeteoCurrent(
    @SerialName("is_day") val isDay: Int, @SerialName("temperature_2m") val temperature: Double
)

enum class WeatherStates(
    val description: String,
) {
    CODE_0("Clear Skies"),
    CODE_1("Mostly Clear"),
    CODE_2("Partly Cloudy"),
    CODE_3("Overcast"),
    CODE_45("Foggy"),
    CODE_48("Foggy"),
    CODE_51("Light Drizzle"),
    CODE_53("Drizzle"),
    CODE_55("Heavy Drizzle"),
    CODE_56("Freezing Drizzle"),
    CODE_57("Heavy Freezing Drizzle"),
    CODE_61("Light Rain"),
    CODE_63("Rainy"),
    CODE_65("Heavy Rain"),
    CODE_66("Freezing Rain"),
    CODE_67("Heavy Freezing Rain"),
    CODE_71("Light Snow"),
    CODE_73("Snowy"),
    CODE_75("Heavy Snow"),
    CODE_77("Snowy"),
    CODE_80("Light Showers"),
    CODE_81("Showers"),
    CODE_82("Heavy Showers"),
    CODE_85("Snowy"),
    CODE_86("Heavy Snow"),
    CODE_95("Thunderstorms"),
    CODE_96("Hail"),
    CODE_99("Heavy Hail");
}