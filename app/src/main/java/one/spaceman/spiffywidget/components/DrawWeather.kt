package one.spaceman.spiffywidget.components

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.state.Weather
import one.spaceman.spiffywidget.theme.textStyle

@SuppressLint("RestrictedApi")
@Composable
fun DrawWeather(weather: Weather?, context: Context) {
    if (weather == null) return

    val style = textStyle.copy(
        color = if (weather.outdated) GlanceTheme.colors.onError else GlanceTheme.colors.secondary
    )

    val intent = context.packageManager.getLaunchIntentForPackage("cz.ackee.ventusky")

    Column(
        modifier = GlanceModifier.padding(bottom = 3.dp)
            .clickable { context.startActivity(intent) },
        horizontalAlignment = Alignment.Start
    ) {
        Row {
            Text(
                text = "${weather.temperature}°",
                style = style.copy(
                    fontSize = textStyle.fontSize?.times(1.3),
                    fontWeight = FontWeight.Bold,
                ),
                modifier = GlanceModifier.padding(end = 10.dp)
            )
            Text(
                text = weather.iconDescription.lowercase(),
                style = style
            )
        }
        Row {
            Text(
                text = "${weather.temperatureLow}°",
                style = style.copy(
                    color = ColorProvider(resId = R.color.blue),
                ),
            )
            Text(text = " ⋄ ", style = style)
            Text(
                text = "${weather.temperatureHigh}°",
                style = style.copy(
                    color = ColorProvider(resId = R.color.red),
                )
            )
            Text(text = " ⋄ ", style = style)
            Text(
                text = "${weather.uvIndex}",
                style = style.copy(
                    color = ColorProvider(resId = R.color.yellow)
                )
            )
        }
    }
}