package one.spaceman.spiffywidget.components

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentWidth
import androidx.glance.text.Text
import one.spaceman.spiffywidget.data.SystemInfo
import one.spaceman.spiffywidget.theme.DrawSpacer
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.theme.itemModifier
import one.spaceman.spiffywidget.theme.textStyle
import java.util.TimeZone

// If in a different timezone, show a clock with both current and home times
@Composable
fun DrawClock(
    context: Context,
) {
    val packageName = context.packageName
    val homeTimeZone = TimeZone.getTimeZone("America/New_York")
    val currentTimeZone = SystemInfo().timeZone
    if (homeTimeZone == currentTimeZone) {
        return
    }

    val homeRemoteView = RemoteViews(packageName, R.layout.clock_component)
    val currentRemoteView = RemoteViews(packageName, R.layout.clock_component)

    Row(
        modifier = itemModifier.background(R.color.peach),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = GlanceModifier.padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HOME",
                style = textStyle,
            )
            AndroidRemoteViews(
                modifier = GlanceModifier.wrapContentWidth(),
                remoteViews = homeRemoteView,
                containerViewId = View.NO_ID,
                content = {
                    homeRemoteView.apply {
                        setCharSequence(
                            R.id.clock_view,
                            "setFormat24Hour",
                            ClockFormat.get24HourFormat()
                        )
                        setCharSequence(
                            R.id.clock_view,
                            "setFormat12Hour",
                            ClockFormat.get12HourFormat()
                        )
                        setString(
                            R.id.clock_view,
                            "setTimeZone",
                            homeTimeZone.id
                        )
                    }
                }
            )
        }
        Spacer(
            GlanceModifier.width(1.dp)
                .background(R.color.grey)
        )
        Column(
            modifier = GlanceModifier.padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentTimeZone.getDisplayName(false, 0),
                style = textStyle,
            )
            AndroidRemoteViews(
                modifier = GlanceModifier.wrapContentWidth(),
                remoteViews = currentRemoteView,
                containerViewId = View.NO_ID,
                content = {
                    currentRemoteView.apply {
                        setCharSequence(
                            R.id.clock_view,
                            "setFormat24Hour",
                            ClockFormat.get24HourFormat()
                        )
                        setCharSequence(
                            R.id.clock_view,
                            "setFormat12Hour",
                            ClockFormat.get12HourFormat()
                        )
                        setString(
                            R.id.clock_view,
                            "setTimeZone",
                            currentTimeZone.id
                        )
                    }
                }
            )
        }
    }
    DrawSpacer()
}

internal object ClockFormat {

    fun get12HourFormat(): SpannableString {
        val timePart = SpannableString("hh:mm a")
        timePart.setSpan(RelativeSizeSpan(1.00f), 0, 4, 0)
        timePart.setSpan(RelativeSizeSpan(0.60f), 5, 7, 0)
        timePart.setSpan(StyleSpan(Typeface.BOLD), 0, 5, 0)
        return timePart
    }

    fun get24HourFormat(): SpannableString {
        val timePart = (SpannableString("HH:mm"))
        timePart.setSpan(RelativeSizeSpan(1.00f), 0, 4, 0)
        timePart.setSpan(StyleSpan(Typeface.BOLD), 0, 5, 0)
        return timePart
    }

}