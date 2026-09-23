package one.spaceman.spiffywidget.widget.components

import android.annotation.SuppressLint
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
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.ui.theme.GlanceTypography
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// If in a different timezone, show a clock with both current and home times
@SuppressLint("RestrictedApi")
@Composable
fun DrawClock(
    context: Context,
    style: GlanceTypography,
    homeTimeZone: String?
) {
    val packageName = context.packageName

    Row(
        modifier = GlanceModifier.fillMaxWidth()
    ) {
        Row {
            Text(
                modifier = GlanceModifier.defaultWeight(),
                text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d")).uppercase(),
                style = style.largeType,
            )
        }
        if (!homeTimeZone.isNullOrEmpty()) {
            val homeTimeZone = ZoneId.of(homeTimeZone)
            val currentTimeZone = ZoneId.systemDefault()
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
                                style.large,
                                style.color.getColor(context).toArgb(),
                                currentTimeZone.id
                            )
                        }
                    )
                    Row(
                        modifier = GlanceModifier.defaultWeight(),
                        horizontalAlignment = Alignment.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.home_24px),
                            contentDescription = "Home Icon",
                            modifier = GlanceModifier.size(style.regular.dp),
                            colorFilter = ColorFilter.tint(style.color)
                        )
                        AndroidRemoteViews(
                            modifier = GlanceModifier.wrapContentSize(),
                            remoteViews = homeRemoteView,
                            containerViewId = View.NO_ID,
                            content = {
                                formatClock(
                                    homeRemoteView,
                                    style.regular,
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