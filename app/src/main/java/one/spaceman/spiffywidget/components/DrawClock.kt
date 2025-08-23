package one.spaceman.spiffywidget.components

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentWidth
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.data.SystemInfo
import java.time.format.DateTimeFormatter
import java.util.TimeZone

// If in a different timezone, show a clock with both current and home times
@Composable
fun DrawClock(
    context: Context,
) {
    val packageName = context.packageName
    val homeTimeZone = TimeZone.getTimeZone("America/New_York")
    val currentTimeZone = SystemInfo().timeZone


    val homeRemoteView = RemoteViews(packageName, R.layout.clock_component)
    val currentRemoteView = RemoteViews(packageName, R.layout.clock_component)

    Column(
        modifier = GlanceModifier.fillMaxWidth().padding(bottom = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = SystemInfo().date.format(DateTimeFormatter.ofPattern("EEEE MMMM d")).uppercase(),
            style = TextStyle(
                fontSize = 24.sp,
                color = GlanceTheme.colors.secondary,
            ),
        )

        if (homeTimeZone != currentTimeZone) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                            setTextViewTextSize(
                                R.id.clock_view,
                                TypedValue.COMPLEX_UNIT_SP,
                                32f
                            )
                        }
                    }
                )
                Spacer(GlanceModifier.width(10.dp))
                Column {
                    Text(
                        text = "HOME",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = GlanceTheme.colors.secondary
                        )
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
                                setTextViewTextSize(
                                    R.id.clock_view,
                                    TypedValue.COMPLEX_UNIT_SP,
                                    14f
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

internal object ClockFormat {

    fun get12HourFormat(): SpannableString {
        val timePart = SpannableString("hh:mma")
        timePart.setSpan(ForegroundColorSpan(Color.WHITE), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        timePart.setSpan(RelativeSizeSpan(0.60f), 5, 6, 0)
        timePart.setSpan(StyleSpan(Typeface.BOLD), 0, 5, 0)
        return timePart
    }

    fun get24HourFormat(): SpannableString {
        val timePart = (SpannableString("HH:mm"))
        timePart.setSpan(ForegroundColorSpan(Color.WHITE), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        timePart.setSpan(StyleSpan(Typeface.BOLD), 0, 5, 0)
        return timePart
    }

}