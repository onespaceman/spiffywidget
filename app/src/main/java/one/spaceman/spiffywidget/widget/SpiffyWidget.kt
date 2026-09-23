package one.spaceman.spiffywidget.widget

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.state.SpiffyWidgetState
import one.spaceman.spiffywidget.state.SpiffyWidgetStateDefinition
import one.spaceman.spiffywidget.ui.theme.GlanceTypography
import one.spaceman.spiffywidget.widget.components.DrawAlarm
import one.spaceman.spiffywidget.widget.components.DrawCalendar
import one.spaceman.spiffywidget.widget.components.DrawClock
import one.spaceman.spiffywidget.widget.components.DrawWeather
import one.spaceman.spiffywidget.worker.WidgetWorkManager

class SpiffyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SpiffyWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            Intent.ACTION_LOCALE_CHANGED, Intent.ACTION_TIMEZONE_CHANGED, Intent.ACTION_BOOT_COMPLETED -> {
                WidgetWorkManager(context).updateNow()
            }

            AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED -> {
                WidgetWorkManager(context).updateNow(arrayOf(WidgetWorkManager.PartialUpdate.ALARM))
            }
        }
    }

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        WidgetWorkManager(context).scheduleUpdate()
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        WidgetWorkManager(context).updateNow()
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WidgetWorkManager(context).cancel()
    }
}

class SpiffyWidget : GlanceAppWidget() {

    override val stateDefinition = SpiffyWidgetStateDefinition

    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {
            GlanceTheme {
                val state = currentState<SpiffyWidgetState>()
                // default text style
                val style = GlanceTypography(GlanceTheme.colors.onSecondaryContainer, 18.sp)
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Column(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .cornerRadius(15.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalAlignment = Alignment.Start,
                    ) {
                        DrawAlarm(context, style, state.alarm)
                        DrawClock(context, style, state.settings.homeTimeZone)
                        DrawWeather(context, style, state.weather, state.settings.weatherApp)
                        DrawCalendar(context, style, state.events, state.alarm)
                    }
                }
                // Secret update button
                Box(
                    modifier = GlanceModifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        modifier = GlanceModifier.wrapContentHeight().clickable {
                            WidgetWorkManager(context).updateNow()
                        },
                        text = " ⬤ ",
                        style = TextStyle(
                            fontSize = 30.sp, color = ColorProvider(resId = R.color.hidden)
                        ),
                    )
                }
            }
        }
    }
}