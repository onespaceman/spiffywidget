package one.spaceman.spiffywidget.theme

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import androidx.glance.unit.ColorProvider

fun formatTime(time: String): String {
    return time.replace("AM", "ᴀᴍ").replace("PM", "ᴘᴍ")
}

@SuppressLint("RestrictedApi")
fun editColor(
    color: Color,
    alpha: Float = color.alpha,
    red: Float = color.red,
    green: Float = color.green,
    blue: Float = color.blue
): ColorProvider {
    return ColorProvider(color.copy(alpha, red, green, blue))
}