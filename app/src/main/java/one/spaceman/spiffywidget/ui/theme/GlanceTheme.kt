package one.spaceman.spiffywidget.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceTheme
import androidx.glance.color.ColorProvider
import androidx.glance.color.ColorProviders
import androidx.glance.text.FontFamily
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextDefaults
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

data class GlanceTypography(
    val color: ColorProvider = TextDefaults.defaultTextColor,
    private val fontSize: TextUnit = 18.sp,
    private val fontWeight: FontWeight? = FontWeight.Normal,
    private val fontStyle: FontStyle? = FontStyle.Normal,
    private val textAlign: TextAlign? = TextAlign.Start,
    private val textDecoration: TextDecoration? = null,
    private val fontFamily: FontFamily? = null
) {
    val defaultColor = color
    val regular = TextStyle(
        color,
        fontSize,
        fontWeight,
        fontStyle,
        textAlign,
        textDecoration,
        fontFamily
    )
    val extraSmall = regular.copy(fontSize = fontSize.times(0.4))
    val small = regular.copy(fontSize = fontSize.times(0.75))
    val large = regular.copy(fontSize = fontSize.times(1.5))
}

// Extension functions
// Return a ColorProvider with an alpha value
fun ColorProvider.withAlpha(context: Context, alpha: Float): ColorProvider  {
    val color = getColor(context).copy(alpha = alpha)
    return ColorProvider(color, color)
}

val GlanceTheme.typography
    @Composable get() = GlanceTypography(color = GlanceTheme.colors.default)

// Default colors
val ColorProviders.default: ColorProvider
    @Composable get() = GlanceTheme.colors.primary

val ColorProviders.onDefault: ColorProvider
    @Composable get() = GlanceTheme.colors.onPrimary

// Extra colors
val ColorProviders.blue: ColorProvider
    @Composable get() = ColorProvider(Blue, Blue)

val ColorProviders.red: ColorProvider
    @Composable get() = ColorProvider(Red, Red)

val ColorProviders.yellow: ColorProvider
    @Composable get() = ColorProvider(Yellow, Yellow)

val ColorProviders.hidden: ColorProvider
    @Composable get() = ColorProvider(Hidden, Hidden)

val ColorProviders.transparent: ColorProvider
    @Composable get() = ColorProvider(Color.Transparent, Color.Transparent)
