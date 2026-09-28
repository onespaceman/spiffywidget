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
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.Text
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.ui.theme.typography
import kotlin.time.Clock

// If in a different timezone, show a clock with both current and home times
@Composable
fun DrawClock(
    context: Context,
    homeTimeZone: String?
) {
    val packageName = context.packageName
    val currentTimeZone = TimeZone.currentSystemDefault()
    val now = Clock.System.now().toLocalDateTime(currentTimeZone)

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(top = 5.dp)
    ) {
        Row {
            Text(
                modifier = GlanceModifier.defaultWeight(),
                text = now.format(LocalDateTime.Format {
                    monthName(MonthNames.ENGLISH_FULL)
                    char(' ')
                    day(Padding.SPACE)
                    char(' ')
                }).uppercase(),
                style = GlanceTheme.typography.large,
            )
        }
        if (!homeTimeZone.isNullOrEmpty()) {
            val homeTimeZone = TimeZone.of(homeTimeZone)
            if (homeTimeZone != currentTimeZone) {
                val homeRemoteView = RemoteViews(packageName, R.layout.clock_component)
                val currentRemoteView = RemoteViews(packageName, R.layout.clock_component)
                Row(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .padding(start = 5.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    AndroidRemoteViews(
                        modifier = GlanceModifier
                            .wrapContentSize(),
                        remoteViews = currentRemoteView,
                        containerViewId = View.NO_ID,
                        content = {
                            formatClock(
                                currentRemoteView,
                                GlanceTheme.typography.large.fontSize!!.value,
                                GlanceTheme.typography.defaultColor.getColor(context).toArgb(),
                                currentTimeZone.id
                            )
                        }
                    )
                    Row(
                        modifier = GlanceModifier.defaultWeight(),
                        horizontalAlignment = Alignment.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val style = GlanceTheme.typography.regular
                        Image(
                            provider = ImageProvider(R.drawable.home_24px),
                            contentDescription = "Home Icon",
                            modifier = GlanceModifier.size(style.fontSize!!.value.dp).padding(top = 1.dp),
                            colorFilter = ColorFilter.tint(style.color)
                        )
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
                }
            }
        }
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