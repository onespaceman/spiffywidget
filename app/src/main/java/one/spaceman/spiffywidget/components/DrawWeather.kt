package one.spaceman.spiffywidget.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
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
import one.spaceman.spiffywidget.theme.DrawSpacer
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.state.Weather
import one.spaceman.spiffywidget.theme.textStyle

@Composable
fun DrawWeather(weather: Weather?, location: String?) {
    if (weather == null) return

    var isExtendedWeather by remember { mutableStateOf(false) }

    Column(
        modifier = GlanceModifier.cornerRadius(5.dp)
            .background(R.color.flamingo)
            .padding(horizontal = 10.dp)
            .clickable {
                isExtendedWeather = !isExtendedWeather
            },
        horizontalAlignment = Alignment.CenterHorizontally
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherIcon(weather.icon)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${weather.temperature}°",
                        style = textStyle.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = GlanceModifier.padding(
                            start = 3.dp,
                            end = 1.dp,
                            bottom = (-3).dp
                        )
                    )
                    Row {
                        Text(
                            text = "${weather.temperatureLow}°",
                            style = textStyle.copy(
                                fontSize = 12.sp,
                                color = ColorProvider(Color(0xDD89b4fa)),
                                fontWeight = FontWeight.Bold
                            ),
                        )
                        Text(
                            text = " | ",
                            style = textStyle.copy(
                                fontSize = 12.sp,
                                color = ColorProvider(Color(0x551E1E2E))
                            ),
                        )
                        Text(
                            text = "${weather.temperatureHigh}°",
                            style = textStyle.copy(
                                fontSize = 12.sp,
                                color = ColorProvider(Color(0xDDF38BA8)),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
//                Text(
//                    text = weather.extra,
//                    style = textStyle,
//                    modifier = GlanceModifier.padding(start = 8.dp)
//                )
            }
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