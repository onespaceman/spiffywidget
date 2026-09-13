package one.spaceman.spiffywidget.components

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.provider.AlarmClock
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.theme.editColor
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// If in a different timezone, show a clock with both current and home times
@Composable
fun DrawClock(
    context: Context,
    alarm: String?,
) {
    val packageName = context.packageName
    val homeTimeZone = ZoneId.of("America/New_York")
    val currentTimeZone = ZoneId.systemDefault()

    val homeRemoteView = RemoteViews(packageName, R.layout.clock_component)
    val currentRemoteView = RemoteViews(packageName, R.layout.clock_component)

    val style = TextStyle(
        color = GlanceTheme.colors.onPrimaryContainer,
        fontSize = 18.sp,
    )

    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = GlanceModifier.padding(bottom = 3.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = GlanceModifier.defaultWeight(),
                text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d")).uppercase(),
                style = style.copy(fontSize = style.fontSize?.times(1.2)),
            )

            // Draw next alarm
            if (!alarm.isNullOrEmpty()) {
                Row(
                    modifier = GlanceModifier.defaultWeight().clickable {
                        context.startActivity(Intent(AlarmClock.ACTION_SHOW_ALARMS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    },
                    horizontalAlignment = Alignment.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        modifier = GlanceModifier.padding(end = 3.dp).size(style.fontSize!!.value.dp),
                        provider = ImageProvider(R.drawable.baseline_alarm_24),
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.tertiary),
                        contentDescription = "Alarm",
                        contentScale = ContentScale.Fit,
                    )
                    Text(
                        text = alarm,
                        style = style
                    )
                }
            }
        }

        if (homeTimeZone != currentTimeZone) {
            val color = style.color.getColor(context).toArgb()

            Row(
                modifier = GlanceModifier.fillMaxWidth().padding(bottom = 10.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                AndroidRemoteViews(
                    modifier = GlanceModifier.wrapContentSize(),
                    remoteViews = currentRemoteView,
                    containerViewId = View.NO_ID,
                    content = {
                        currentRemoteView.apply {
                            setCharSequence(
                                R.id.clock_view,
                                "setFormat24Hour",
                                ClockFormat.get24HourFormat(color)
                            )
                            setCharSequence(
                                R.id.clock_view,
                                "setFormat12Hour",
                                ClockFormat.get12HourFormat(color)
                            )
                            setString(
                                R.id.clock_view,
                                "setTimeZone",
                                currentTimeZone.id
                            )
                            setTextViewTextSize(
                                R.id.clock_view,
                                TypedValue.COMPLEX_UNIT_SP,
                                style.fontSize!!.times(1.25).value
                            )
                        }
                    }
                )
                Row(
                    modifier = GlanceModifier.defaultWeight().padding(top = 3.dp),
                    horizontalAlignment = Alignment.End,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        text = "HOME: ",
                        style = style.copy(
                            color = editColor(style.color.getColor(context), alpha = 0.6f),
                            fontSize = style.fontSize?.times(0.6),
                        )
                    )
                    AndroidRemoteViews(
                        modifier = GlanceModifier.wrapContentSize(),
                        remoteViews = homeRemoteView,
                        containerViewId = View.NO_ID,
                        content = {
                            homeRemoteView.apply {
                                setCharSequence(
                                    R.id.clock_view,
                                    "setFormat24Hour",
                                    ClockFormat.get24HourFormat(color)
                                )
                                setCharSequence(
                                    R.id.clock_view,
                                    "setFormat12Hour",
                                    ClockFormat.get12HourFormat(color)
                                )
                                setString(
                                    R.id.clock_view,
                                    "setTimeZone",
                                    homeTimeZone.id
                                )
                                setTextViewTextSize(
                                    R.id.clock_view,
                                    TypedValue.COMPLEX_UNIT_SP,
                                    style.fontSize!!.times(0.6).value
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

    fun get12HourFormat(color: Int): SpannableString {
        return SpannableString("E hh:mma").apply {
            setSpan(ForegroundColorSpan(color), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(RelativeSizeSpan(0.60f), 7, 8, 0)
            setSpan(StyleSpan(Typeface.BOLD), 3, 7, 0)
        }
    }

    fun get24HourFormat(color: Int): SpannableString {
        return (SpannableString("E HH:mm")).apply {
            setSpan(ForegroundColorSpan(color), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD), 3, length - 1, 0)
        }
    }

}