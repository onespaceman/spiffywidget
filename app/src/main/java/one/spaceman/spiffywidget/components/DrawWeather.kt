package one.spaceman.spiffywidget.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.unit.ColorProvider
import one.spaceman.spiffywidget.state.Weather
import one.spaceman.spiffywidget.theme.DrawSpacer
import one.spaceman.spiffywidget.theme.textStyle

@Composable
fun DrawWeather(weather: Weather?, location: String?) {
    if (weather == null) return

    var isExtendedWeather by remember { mutableStateOf(false) }

    Column(
        modifier = GlanceModifier.clickable {
                isExtendedWeather = !isExtendedWeather
            },
        horizontalAlignment = Alignment.Start
    ) {

        if (isExtendedWeather) {

            if (!location.isNullOrEmpty()) {
                Text(
                    text = location,
                    style = textStyle.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.wrapContentSize().padding(top = 10.dp)
                )
            }

            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                weather.forecast.forEach {
                    Column(
                        modifier = GlanceModifier.defaultWeight().padding(bottom = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = " ${it.temperature}°",
                            style = textStyle.copy(
                                textAlign = TextAlign.Center
                            )
                        )
                        WeatherIcon(it.icon)
                        Text(
                            text = it.time,
                            style = textStyle.copy(
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }

        } else {
            Row {
                Text(
                    text = "${weather.temperature}°",
                    style = textStyle.copy(
                        fontSize = textStyle.fontSize?.times(1.3),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.padding(end = 10.dp)
                )
                Text(
                    text = weather.iconDescription.lowercase(),
                    style = textStyle
                )
            }
            Row {
                Text(
                    text = "${weather.temperatureLow}°",
                    style = textStyle.copy(
                        color = ColorProvider(Color(0xFF89b4fa)),
                    ),
                )
                Text(
                    text = " ⋄ ",
                    style = textStyle
                )
                Text(
                    text = "${weather.temperatureHigh}°",
                    style = textStyle.copy(
                        color = ColorProvider(Color(0xFFF38BA8)),
                    )
                )
            }
//            if (weather.extra.isNotBlank()) {
//                Row {
//                    Text(
//                        text = weather.extra.lowercase(),
//                        style = textStyle
//                    )
//                }
//            }
        }
    }
    DrawSpacer()
}

@Composable
fun WeatherIcon(icon: List<Int>) {
    Box(
        modifier = GlanceModifier
    ) {
        icon.forEach {
            Image(
                modifier = GlanceModifier
                    .padding(end = 5.dp)
                    .size(50.dp),
                provider = ImageProvider(it),
                contentDescription = "$it",
                contentScale = ContentScale.Fit
            )
        }
    }
}