package one.spaceman.spiffywidget.components

import android.content.Context
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import one.spaceman.spiffywidget.R
import java.time.ZoneId

// If in a different timezone, show a clock with both current and home times
@Composable
fun DrawClock(
    context: Context,
    style: TextStyle,
) {
    val packageName = context.packageName
    val homeTimeZone = ZoneId.of("America/New_York")
    val currentTimeZone = ZoneId.systemDefault()

    val homeRemoteView = RemoteViews(packageName, R.layout.clock_component)
    val currentRemoteView = RemoteViews(packageName, R.layout.clock_component)

    if (homeTimeZone != currentTimeZone) {
        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = GlanceModifier
//                        .background(GlanceTheme.colors.tertiaryContainer)
                        .cornerRadius(25.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AndroidRemoteViews(
                        modifier = GlanceModifier.wrapContentSize()
                            .background(GlanceTheme.colors.tertiaryContainer)
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        remoteViews = currentRemoteView,
                        containerViewId = View.NO_ID,
                        content = {
                            formatClock(
                                currentRemoteView,
                                style.fontSize!!.times(2).value,
                                GlanceTheme.colors.onTertiaryContainer.getColor(context).toArgb(),
                                currentTimeZone.id
                            )
                        }
                    )
                    Column(
                        modifier = GlanceModifier
                            .background(GlanceTheme.colors.secondaryContainer)
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "HOME",
                            style = style.copy(fontSize = style.fontSize!!.times(0.8))
                        )
                        AndroidRemoteViews(
                            modifier = GlanceModifier.wrapContentSize(),
                            remoteViews = homeRemoteView,
                            containerViewId = View.NO_ID,
                            content = {
                                formatClock(
                                    homeRemoteView,
                                    style.fontSize!!.times(1.2).value,
                                    GlanceTheme.colors.onSecondaryContainer.getColor(context).toArgb(),
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
                setSpan(ForegroundColorSpan(textColor), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(StyleSpan(Typeface.BOLD), 0, length - 1, 0)
            }
        )

        setCharSequence(
            R.id.clock_view,
            "setFormat12Hour",
            SpannableString("hh:mma").apply {
                setSpan(ForegroundColorSpan(textColor), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(RelativeSizeSpan(0.60f), 5, 6, 0)
                setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)
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