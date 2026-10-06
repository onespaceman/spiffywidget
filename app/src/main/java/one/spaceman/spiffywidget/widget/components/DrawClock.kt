package one.spaceman.spiffywidget.widget.components

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.layout.wrapContentSize
import kotlinx.datetime.TimeZone
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.ui.theme.Colors
import one.spaceman.spiffywidget.ui.theme.typography

@Composable
fun DrawClock(
    context: Context,
    homeTimeZone: String?,
) {
    if (homeTimeZone.isNullOrEmpty()) return

    val currentTimeZone = TimeZone.currentSystemDefault()
    val homeTimeZone = TimeZone.of(homeTimeZone)
    if (homeTimeZone != currentTimeZone) {
        val homeRemoteView = RemoteViews(context.packageName, R.layout.clock_component)
        val style = GlanceTheme.typography.smaller.copy(color = Colors.background)

        Pill(
            icon = ImageProvider(R.drawable.home_24px),
            content = {
                AndroidRemoteViews(
                    modifier = GlanceModifier.wrapContentSize(),
                    remoteViews = homeRemoteView,
                    containerViewId = View.NO_ID,
                    content = {
                        formatClock(
                            homeRemoteView,
                            style.fontSize!!.value,
                            style.color.getColor(context).toArgb(),
                            homeTimeZone.id
                        )
                    }
                )
            }
        )
    }
}

fun formatClock(view: RemoteViews, textSize: Float, textColor: Int, timeZone: String) {
    view.apply {
        setCharSequence(
            R.id.clock_view,
            "setFormat24Hour",
            SpannableString("HH:mm").apply {
                setSpan(ForegroundColorSpan(textColor), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//                setSpan(StyleSpan(Typeface.BOLD), 0, 4, 0)
            }
        )

        setCharSequence(
            R.id.clock_view,
            "setFormat12Hour",
            SpannableString("hh:mma").apply {
                setSpan(ForegroundColorSpan(textColor), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(RelativeSizeSpan(0.60f), 5, 6, 0)
//                setSpan(StyleSpan(Typeface.BOLD), 0, 5, 0)
            }
        )
        setString(
            R.id.clock_view,
            "setTimeZone",
            timeZone
        )
        setTextViewTextSize(
            R.id.clock_view,
            TypedValue.COMPLEX_UNIT_SP,
            textSize
        )
    }
}