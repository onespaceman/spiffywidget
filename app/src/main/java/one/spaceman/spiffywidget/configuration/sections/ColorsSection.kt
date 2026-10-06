package one.spaceman.spiffywidget.configuration.sections

import android.app.WallpaperManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColor
import one.spaceman.spiffywidget.configuration.SpiffyConfigurationActivity
import one.spaceman.spiffywidget.configuration.components.NewCard
import one.spaceman.spiffywidget.configuration.components.NewSwatch
import one.spaceman.spiffywidget.ui.theme.WidgetColorOptions
import one.spaceman.spiffywidget.ui.theme.getColors

@Composable
fun ColorsSection(
    context: Context,
    state: SpiffyConfigurationActivity.State
) {
    NewCard(
        "Colors"
    ) {
        // Preview
        val (contentColor, backgroundColor) = state.settings.color.getColors(state.settings.invertColors)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(paintBackground(context)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
            ) {
                Text(
                    text = "Preview of ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor,
                    modifier = Modifier.padding(5.dp)
                )
                Text(
                    text = "Widget Text",
                    style = MaterialTheme.typography.bodyLarge,
                    color = backgroundColor,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(contentColor).padding(5.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                items = WidgetColorOptions.entries,
            ) { color ->
                val (color1, color2) = color.getColors(state.settings.invertColors)
                NewSwatch(
                    color1 = color1,
                    color2 = color2,
                    selected = state.settings.color == color,
                    onClick = {
                        state.setSettings(color = color)
                    }
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Invert colors",
                style = MaterialTheme.typography.titleLarge
            )
            Switch(
                checked = state.settings.invertColors,
                onCheckedChange = {
                    state.setSettings(invertColors = !state.settings.invertColors)
                }
            )

        }
    }
}

@Composable
internal fun paintBackground(context: Context): Brush {
    val colors = WallpaperManager.getInstance(context).getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
    val colorList = if (colors != null) {
        listOfNotNull(colors.primaryColor, colors.colorHints.toColor(), colors.secondaryColor).map { Color(it.toArgb()) }
    } else {
        listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceContainer)
    }
    return Brush.horizontalGradient(colorList)
}