package one.spaceman.spiffywidget.theme

import androidx.compose.ui.unit.sp
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle

val textStyle = TextStyle(
    fontSize = 18.sp,
    fontWeight = FontWeight.Medium,
)

fun formatTime(time: String): String {
    return time.replace("AM", "ᴀᴍ")
        .replace("PM", "ᴘᴍ")
}