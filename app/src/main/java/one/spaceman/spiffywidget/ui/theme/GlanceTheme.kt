package one.spaceman.spiffywidget.ui.theme

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.glance.text.FontFamily
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextDefaults
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

fun formatTime(time: String): String {
    return time.replace("AM", "ᴀᴍ").replace("PM", "ᴘᴍ")
}

data class GlanceTypography(
    val color: ColorProvider = TextDefaults.defaultTextColor,
    private val fontSize: TextUnit = 18.sp,
    private val fontWeight: FontWeight? = FontWeight.Normal,
    private val fontStyle: FontStyle? = FontStyle.Normal,
    private val textAlign: TextAlign? = TextAlign.Start,
    private val textDecoration: TextDecoration? = null,
    private val fontFamily: FontFamily? = null
){
    private val baseStyle = TextStyle(
        color,
        fontSize,
        fontWeight,
        fontStyle,
        textAlign,
        textDecoration,
        fontFamily
    )

    val regular = fontSize.value
    val extraSmall = regular.times(0.4f)
    val small = regular.times(0.75f)
    val large = regular.times(1.5f)

    val regularType = baseStyle.copy(
        fontSize = regular.sp,
    )
    val extraSmallType = baseStyle.copy(
        fontSize = extraSmall.sp
    )
    val smallType = baseStyle.copy(
        fontSize = small.sp
    )
    val largeType = baseStyle.copy(
        fontSize = large.sp
    )
}
