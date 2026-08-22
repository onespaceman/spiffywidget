package one.spaceman.spiffywidget.components

import android.annotation.SuppressLint
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
import androidx.glance.unit.ColorProvider
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.state.Weather
import one.spaceman.spiffywidget.theme.textStyle

@SuppressLint("RestrictedApi")
@Composable
fun DrawWeather(context: Context, weather: Weather?) {
    if (weather == null) return

    val style = textStyle.copy(color = GlanceTheme.colors.secondary)

    val intent = context.packageManager.getLaunchIntentForPackage("cz.ackee.ventusky")

    Column(
        modifier = GlanceModifier.padding(bottom = 3.dp).fillMaxWidth()
            .clickable { context.startActivity(intent) }, horizontalAlignment = Alignment.Start
    ) {
        Row {
            Text(
                text = "${weather.temperature}°", style = style.copy(
                    fontSize = textStyle.fontSize?.times(1.4),
                    fontWeight = FontWeight.Bold,
                ), modifier = GlanceModifier.padding(end = 10.dp)
            )
            Text(
                text = weather.description.lowercase(), style = style
            )
            Text(
                text = weather.extra, style = style.copy(
                    fontSize = style.fontSize?.times(0.8), textAlign = TextAlign.End
                ), modifier = GlanceModifier.fillMaxWidth()
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = GlanceModifier.size(20.dp),
                provider = ImageProvider(R.drawable.down_arrow),
                colorFilter = ColorFilter.tint(ColorProvider(R.color.blue)),
                contentDescription = "down arrow",
                contentScale = ContentScale.Fit
            )
            Text(
                text = "${weather.temperatureLow}° ",
                style = style.copy(
                    color = ColorProvider(resId = R.color.blue),
                ),
            )
            Image(
                modifier = GlanceModifier.size(20.dp),
                provider = ImageProvider(R.drawable.up_arrow),
                colorFilter = ColorFilter.tint(ColorProvider(R.color.red)),
                contentDescription = "up arrow",
                contentScale = ContentScale.Fit
            )
            Text(
                text = "${weather.temperatureHigh}° ", style = style.copy(
                    color = ColorProvider(resId = R.color.red),
                )
            )
            Image(
                modifier = GlanceModifier.size(20.dp),
                provider = ImageProvider(R.drawable.sun),
                colorFilter = ColorFilter.tint(ColorProvider(R.color.yellow)),
                contentDescription = "sun",
                contentScale = ContentScale.Fit
            )
            Text(
                text = "${weather.uvIndex} ", style = style.copy(
                    color = ColorProvider(R.color.yellow)
                )
            )
        }
    }
}