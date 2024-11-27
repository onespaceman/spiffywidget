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
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
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
                    style = textStyle.copy(),
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
                            text = "${it.temperature}°",
                            style = textStyle
                        )
                        Image(
                            provider = ImageProvider(it.icon),
                            modifier = GlanceModifier.size(45.dp),
                            contentDescription = it.iconDescription,
                        )
                        Text(
                            text = it.time,
                            style = textStyle.copy(fontSize = 12.sp)
                        )
                    }
                }
            }

        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = GlanceModifier
                        .padding(end = 5.dp)
                        .size(50.dp),
                    provider = ImageProvider(weather.icon),
                    contentDescription = weather.iconDescription,
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "${weather.temperatureLow}°",
                    style = textStyle.copy(
                        fontSize = 12.sp,
                        color = ColorProvider(Color(0xAA89b4fa))
                    ),
                    modifier = GlanceModifier.padding(top = 7.dp)
                )
                Text(
                    text = "${weather.temperature}°",
                    style = textStyle.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = GlanceModifier.padding(start = 3.dp, end = 1.dp)
                )
                Text(
                    text = "${weather.temperatureHigh}°",
                    style = textStyle.copy(
                        fontSize = 12.sp,
                        color = ColorProvider(Color(0xAAF38BA8))
                    ),
                    modifier = GlanceModifier.padding(top = 7.dp)
                )
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