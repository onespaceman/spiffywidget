package one.spaceman.spiffywidget.widget.components

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
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
import one.spaceman.spiffywidget.ui.theme.GlanceTypography

@SuppressLint("RestrictedApi")
@Composable
fun DrawWeather(
    context: Context,
    style: GlanceTypography,
    weather: Weather?,
    weatherApp: String?,
) {
    if (weather == null) return

    val intent = if(!weatherApp.isNullOrEmpty()) {
        context.packageManager.getLaunchIntentForPackage(weatherApp)
    } else null

    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { context.startActivity(intent) },
    ) {
        Row(
            modifier = GlanceModifier.padding(bottom = (-3).dp)
        ) {
            Text(
                text = "${weather.temperature}°",
                style = style.copy(fontWeight = FontWeight.Bold).largeType,
                modifier = GlanceModifier.padding(end = 7.dp)
            )
            Text(
                text = weather.code.label.lowercase(),
                style = style.regularType
            )
            Text(
                text = weather.location,
                style = style.copy(textAlign = TextAlign.End).smallType,
                modifier = GlanceModifier.fillMaxWidth()
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = GlanceModifier.size(15.dp),
                provider = ImageProvider(R.drawable.down_arrow),
                colorFilter = ColorFilter.tint(ColorProvider(R.color.blue)),
                contentDescription = "down arrow",
                contentScale = ContentScale.Fit
            )
            Text(
                text = "${weather.temperatureLow}° ",
                style = style.copy(color = ColorProvider(resId = R.color.blue)).regularType,
            )
            Image(
                modifier = GlanceModifier.size(15.dp),
                provider = ImageProvider(R.drawable.up_arrow),
                colorFilter = ColorFilter.tint(ColorProvider(R.color.red)),
                contentDescription = "up arrow",
                contentScale = ContentScale.Fit
            )
            Text(
                text = "${weather.temperatureHigh}° ",
                style = style.copy(color = ColorProvider(resId = R.color.red)).regularType
            )
            Image(
                modifier = GlanceModifier.size(20.dp),
                provider = ImageProvider(R.drawable.sun),
                colorFilter = ColorFilter.tint(ColorProvider(R.color.yellow)),
                contentDescription = "sun",
                contentScale = ContentScale.Fit
            )
            Text(
                text = "${weather.uvIndex} ",
                style = style.copy(color = ColorProvider(R.color.yellow)).regularType
            )
            Text(
                text = weather.extra,
                style = style.copy(textAlign = TextAlign.End).smallType,
                modifier = GlanceModifier.fillMaxWidth()
            )
        }
    }
}