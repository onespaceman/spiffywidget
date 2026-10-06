package one.spaceman.spiffywidget.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.color.ColorProvider
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
    val smaller = regular.copy(fontSize = fontSize.times(0.85))
    val small = regular.copy(fontSize = fontSize.times(0.75))
    val extraSmall = regular.copy(fontSize = fontSize.times(0.55))
    val large = regular.copy(fontSize = fontSize.times(1.5))
    val largeBold = regular.copy(fontSize = fontSize.times(1.5), fontWeight = FontWeight.Bold)
}

// Extension functions
// Return a ColorProvider with an alpha value
@Composable
fun ColorProvider.withAlpha(alpha: Float): ColorProvider {
    val color = getColor(LocalContext.current).copy(alpha = alpha)
    return ColorProvider(color, color)
}

val GlanceTheme.typography
    @Composable get() = GlanceTypography(color = Colors.content)

// Custom color providers
object Colors {
    // Default Colors
    val content: ColorProvider
        @Composable get() = LocalContentColor.current
    val background: ColorProvider
        @Composable get() = LocalBackgroundColor.current

    // Custom colors
    private val useDarkColors: Boolean
        @Composable get() = LocalUseDarkColors.current
    val red: ColorProvider
        @Composable get() {
            return if (useDarkColors) {
                ColorProvider(DarkRed, DarkRed)
            } else {
                ColorProvider(Red, Red)
            }
        }
    val blue: ColorProvider
        @Composable get() {
            return if (useDarkColors) {
                ColorProvider(DarkBlue, DarkBlue)
            } else {
                ColorProvider(Blue, Blue)
            }
        }
    val yellow: ColorProvider
        @Composable get() {
            return if (useDarkColors) {
                ColorProvider(DarkYellow, DarkYellow)
            } else {
                ColorProvider(Yellow, Yellow)
            }
        }
    val transparent = ColorProvider(Color.Transparent, Color.Transparent)
}

@Composable
fun SpiffyWidgetColors(
    color: ColorProvider,
    backgroundColor: ColorProvider,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val useDarkColors = color.getColor(context).luminance() < 0.5
    CompositionLocalProvider(
        LocalContentColor provides color,
        LocalBackgroundColor provides backgroundColor,
        LocalUseDarkColors provides useDarkColors,
        content = content
    )
}

val LocalContentColor = staticCompositionLocalOf { ColorProvider(Color.Black, Color.White) }
val LocalBackgroundColor = staticCompositionLocalOf { ColorProvider(Color.White, Color.Black) }
val LocalUseDarkColors = staticCompositionLocalOf { false }

enum class WidgetColorOptions {
    PRIMARY,
    SECONDARY,
    TERTIARY,
    SURFACE,
}

@Composable
internal fun WidgetColorOptions.getColors(isInverted: Boolean = false): Pair<Color, Color> =
    when (this) {
        WidgetColorOptions.PRIMARY -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        WidgetColorOptions.SECONDARY -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        WidgetColorOptions.TERTIARY -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
        WidgetColorOptions.SURFACE -> MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface
    }.also { return if (isInverted) it.invert() else it }

@Composable
internal fun WidgetColorOptions.getColorProviders(isInverted: Boolean = false): Pair<ColorProvider, ColorProvider> =
    when (this) {
        WidgetColorOptions.PRIMARY -> GlanceTheme.colors.primary to GlanceTheme.colors.onPrimary
        WidgetColorOptions.SECONDARY -> GlanceTheme.colors.secondary to GlanceTheme.colors.onSecondary
        WidgetColorOptions.TERTIARY -> GlanceTheme.colors.tertiary to GlanceTheme.colors.onTertiary
        WidgetColorOptions.SURFACE -> GlanceTheme.colors.surface to GlanceTheme.colors.onSurface
    }.also { return if (isInverted) it.invert() else it }

// invert a Pair
fun <A, B> Pair<A, B>.invert(): Pair<B, A> = second to first