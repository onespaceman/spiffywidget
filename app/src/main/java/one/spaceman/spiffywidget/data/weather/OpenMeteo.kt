package one.spaceman.spiffywidget.data.weather

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until
import kotlinx.serialization.json.Json
import one.spaceman.spiffywidget.state.Weather
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.Instant

object WeatherAdapter {

    private const val BASE_URL = "https://api.open-meteo.com/v1/forecast"

    private val httpClient = HttpClient(OkHttp) {
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
            exponentialDelay()
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private suspend fun getWeather(
        latitude: Double, longitude: Double, timeZone: String
    ): OpenMeteoResponse {
        return httpClient.get(urlString = BASE_URL) {
            header(HttpHeaders.UserAgent, "Spiffy Widgett, platform: Android")
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("timezone", timeZone)
            parameter("timeformat", "unixtime")
            parameter("temperature_unit", "fahrenheit")
            parameter("precipitation_unit", "inch")
            parameter("wind_speed_unit", "mph")
            parameter(
                "daily",
                "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,precipitation_sum,uv_index_max"
            )
            parameter("current", "is_day,temperature_2m")
        }.body()
    }

    suspend fun getFormatedWeather(latitude: Double, longitude: Double): Weather? {
        try {
            val timezone = TimeZone.currentSystemDefault()
            val now = Clock.System.now()
            val response = getWeather(latitude, longitude, timezone.id)

            val sunrise = Instant.fromEpochSeconds(response.daily.sunriseEpochSeconds.first())
            val sunset = Instant.fromEpochSeconds(response.daily.sunsetEpochSeconds.first())

            val extra = if (now.until(sunrise, DateTimeUnit.HOUR) in 0..5) {
                sunrise.toLocalDateTime(timezone).format(LocalDateTime.Format {
                    chars("Sunrise at ")
                    amPmHour()
                    chars(":")
                    minute()
                    amPmMarker("ᴀᴍ", "ᴘᴍ")
                })
            } else if (now.until(sunset, DateTimeUnit.HOUR) in 0..5) {
                sunset.toLocalDateTime(timezone).format(LocalDateTime.Format {
                    chars("Sunset at ")
                    amPmHour()
                    chars(":")
                    minute()
                    amPmMarker("ᴀᴍ", "ᴘᴍ")
                })
            } else ""

            return Weather(
                lastUpdate = now,
                temperature = response.current.temperature.roundToInt(),
                temperatureLow = response.daily.temperatureMin.first().roundToInt(),
                temperatureHigh = response.daily.temperatureMax.first().roundToInt(),
                uvIndex = response.daily.uvIndex.first().roundToInt(),
                extra = extra,
                code = WeatherCodes.fromCode(response.daily.weatherCode.first()),
            )
        } catch (e: Exception) {
            Log.e("OpenMeteo-Response", e.message.toString())
            return null
        }
    }
}