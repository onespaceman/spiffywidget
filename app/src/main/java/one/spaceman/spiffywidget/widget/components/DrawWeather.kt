package one.spaceman.spiffywidget.widget.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.state.Weather
import one.spaceman.spiffywidget.ui.theme.Colors
import one.spaceman.spiffywidget.ui.theme.typography

@Composable
fun DrawWeather(
    context: Context,
    weather: Weather?,
    weatherApp: String?,
) {
    if (weather == null) return

    val intent = if (!weatherApp.isNullOrEmpty()) {
        context.packageManager.getLaunchIntentForPackage(weatherApp)
    } else null

    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { context.startActivity(intent) },
    ) {
        Row(
            modifier = GlanceModifier.padding(bottom = (-3).dp)
        ) {
            Text(
                text = "${weather.temperature}°",
                style = GlanceTheme.typography.large.copy(fontWeight = FontWeight.Bold),
                modifier = GlanceModifier.padding(end = 7.dp)
            )
            Text(
                text = weather.code.label.lowercase(),
                style = GlanceTheme.typography.regular,
            )
            if (weather.location != null) {
                Text(
                    text = weather.location,
                    style = GlanceTheme.typography.small.copy(textAlign = TextAlign.End),
                    modifier = GlanceModifier.fillMaxWidth()
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = GlanceModifier.size(15.dp),
                provider = ImageProvider(R.drawable.down_arrow),
                colorFilter = ColorFilter.tint(Colors.blue),
                contentDescription = "down arrow",
                contentScale = ContentScale.Fit
            )
            Text(
                text = "${weather.temperatureLow}° ",
                style = GlanceTheme.typography.regular.copy(Colors.blue),
            )
            Image(
                modifier = GlanceModifier.size(15.dp),
                provider = ImageProvider(R.drawable.up_arrow),
                colorFilter = ColorFilter.tint(Colors.red),
                contentDescription = "up arrow",
                contentScale = ContentScale.Fit
            )
            Text(
                text = "${weather.temperatureHigh}° ",
                style = GlanceTheme.typography.regular.copy(Colors.red),
            )
            Image(
                modifier = GlanceModifier.size(20.dp),
                provider = ImageProvider(R.drawable.sun),
                colorFilter = ColorFilter.tint(Colors.yellow),
                contentDescription = "sun",
                contentScale = ContentScale.Fit
            )
            Text(
                text = "${weather.uvIndex} ",
                style = GlanceTheme.typography.regular.copy(Colors.yellow),
            )
            Text(
                text = weather.extra,
                style = GlanceTheme.typography.small.copy(textAlign = TextAlign.End),
                modifier = GlanceModifier.fillMaxWidth()
            )
        }
    }
}