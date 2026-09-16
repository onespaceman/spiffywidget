package one.spaceman.spiffywidget.theme

fun formatTime(time: String): String {
    return time.replace("AM", "ᴀᴍ").replace("PM", "ᴘᴍ")
}