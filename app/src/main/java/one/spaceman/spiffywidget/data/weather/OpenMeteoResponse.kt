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
    @SerialName("is_day")val isDay: Int,
    @SerialName("temperature_2m") val temperature: Double
)

enum class WeatherCodes(
    val code: Int,
    val label: String
) {
    CLEAR_SKIES(0, "Clear Skies"),
    MOSTLY_CLEAR(1, "Mostly Clear"),
    PARTLY_CLOUDY(2, "Partly Cloudy"),
    OVERCAST(3, "Overcast"),
    FOGGY(45, "Foggy"),
    RIME_FOG(48, "Rime Fog"),
    LIGHT_DRIZZLE(51, "Light Drizzle"),
    DRIZZLE(53, "Drizzle"),
    HEAVY_DRIZZLE(55, "Heavy Drizzle"),
    FREEZING_DRIZZLE(56, "Freezing Drizzle"),
    HEAVY_FREEZING_DRIZZLE(57, "Heavy Freezing Drizzle"),
    LIGHT_RAIN(67, "Light Rain"),
    RAINY(63, "Rainy"),
    HEAVY_RAIN(65, "Heavy Rain"),
    FREEZING_RAIN(66, "Freezing Rain"),
    HEAVY_FREEZING_RAIN(67, "Heavy Freezing Rain"),
    LIGHT_SNOW(71, "Light Snow"),
    SNOWY(73, "Snowy"),
    HEAVY_SNOW(75, "Heavy Snow"),
    SNOW_GRAINS(77, "Light Snow"),
    LIGHT_SHOWERS(80, "Light Showers"),
    SHOWERS(81, "Showers"),
    HEAVY_SHOWERS(82, "Heavy Showers"),
    SNOW_SHOWERS(85, "Snowy"),
    HEAVY_SNOW_SHOWERS(86, "Heavy Snow"),
    THUNDERSTORMS(95, "Thunderstorms"),
    HAIL(96, "Hail"),
    HEAVY_HAIL(99, "Heavy Hail"),
    UNKNOWN(-1, "");

    companion object {
        private val byCode = entries.associateBy(WeatherCodes::code)
        fun fromCode(code: Int): WeatherCodes = byCode[code] ?: UNKNOWN
    }
}