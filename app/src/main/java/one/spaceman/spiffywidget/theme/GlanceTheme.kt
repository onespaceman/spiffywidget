package one.spaceman.spiffywidget.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

val textStyle = TextStyle(
    fontSize = 14.sp,
    color = ColorProvider(Color(0xFF1E1E2E))
)

val itemModifier = GlanceModifier
//    .fillMaxWidth()
    .padding(horizontal = 10.dp, vertical = 5.dp)
    .cornerRadius(5.dp)

@Composable
fun DrawSpacer() {
    Spacer(GlanceModifier.height(10.dp))
}
