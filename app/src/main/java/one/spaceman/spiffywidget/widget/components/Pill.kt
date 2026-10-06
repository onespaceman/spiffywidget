package one.spaceman.spiffywidget.widget.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import one.spaceman.spiffywidget.ui.theme.Colors
import one.spaceman.spiffywidget.ui.theme.typography

@Composable
fun Pill(
    icon: ImageProvider? = null,
    text: String? = null,
    onclick: () -> Unit? = {},
    content: @Composable () -> Unit,
    contentColor: ColorProvider = Colors.background,
    backgroundColor: ColorProvider = Colors.content,
) {
    Column(
        modifier = GlanceModifier.padding(vertical = 5.dp, horizontal = 7.dp),
    ) {
        Row(
            modifier = GlanceModifier
                .padding(5.dp, 3.dp)
                .cornerRadius(25.dp)
                .background(backgroundColor)
                .clickable { onclick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon != null) {
                Image(
                    provider = icon,
                    contentDescription = "Alarm Icon",
                    modifier = GlanceModifier.size(GlanceTheme.typography.smaller.fontSize!!.value.dp),
                    colorFilter = ColorFilter.tint(contentColor),
                )
            }
            if (text != null) {
                Text(
                    text = " $text",
                    style = GlanceTheme.typography.smaller.copy(contentColor),
                )
            }
            content()
        }
    }
}