package one.spaceman.spiffywidget.data.weather

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
import kotlinx.serialization.json.Json
import one.spaceman.spiffywidget.data.SystemInfo
import one.spaceman.spiffywidget.state.Weather
import one.spaceman.spiffywidget.theme.formatTime
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

object WeatherAdapter {

    private const val BASE_URL = "https://api.open-meteo.com/v1/forecast"

    private val httpClient = HttpClient {
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
            header(HttpHeaders.UserAgent, "Spiffy Widget, platform: Android")
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

    suspend fun getFormatedWeather(
        context: Context, info: SystemInfo, latitude: Double, longitude: Double
    ): Weather? {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.INTERNET,
            ) == PackageManager.PERMISSION_DENIED
        ) {
            return null
        }

        try {
            val response = getWeather(latitude, longitude, info.timeZone.id)

            val timezone = info.timeZone.toZoneId()

            val sunset = Instant.ofEpochSecond(response.daily.sunsetEpochSeconds.first())
            val sunrise = Instant.ofEpochSecond(response.daily.sunriseEpochSeconds.first())
            val extra = if (ChronoUnit.HOURS.between(info.now, sunrise) in 1..2) {
                "Sunrise at ${
                    formatTime(
                        LocalDateTime.ofInstant(sunrise, timezone)
                            .format(DateTimeFormatter.ofPattern("h:mma"))
                    )
                }"
            } else if (ChronoUnit.HOURS.between(info.now, sunset) in -21..99) {
                "Sunset at ${
                    formatTime(
                        LocalDateTime.ofInstant(sunset, timezone)
                            .format(DateTimeFormatter.ofPattern("h:mma"))
                    )
                }"
            } else ""

            return Weather(
                lastUpdate = info.now.epochSecond,
                temperature = response.current.temperature.roundToInt(),
                temperatureLow = response.daily.temperatureMin.first().roundToInt(),
                temperatureHigh = response.daily.temperatureMax.first().roundToInt(),
                uvIndex = response.daily.uvIndex.first().roundToInt(),
                extra = extra,
                description = WeatherStates.valueOf("CODE_${response.daily.weatherCode.first()}").description,
            )
        } catch (e: Exception) {
            Log.e("OpenMeteo-Response", e.message.toString())
            return null
        }
    }
}