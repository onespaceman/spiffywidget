package one.spaceman.spiffywidget

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
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import one.spaceman.spiffywidget.components.DrawAlarm
import one.spaceman.spiffywidget.components.DrawClock
import one.spaceman.spiffywidget.components.DrawEvents
import one.spaceman.spiffywidget.components.DrawWeather
import one.spaceman.spiffywidget.state.SpiffyWidgetState
import one.spaceman.spiffywidget.state.SpiffyWidgetStateDefinition
import one.spaceman.spiffywidget.worker.WidgetWorkManager
import one.spaceman.spiffywidget.worker.WidgetWorkManager.Companion.PartialUpdate

class SpiffyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SpiffyWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            Intent.ACTION_LOCALE_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_BOOT_COMPLETED -> {
                WidgetWorkManager(context).updateNow()
            }

            android.app.AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED -> {
                WidgetWorkManager(context).updateNow(PartialUpdate.ALARM)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
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

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {
            GlanceTheme {
                val state = currentState<SpiffyWidgetState>()

                Box(GlanceModifier.fillMaxSize().padding(vertical = 2.dp)) {
                    Column(
                        modifier = GlanceModifier.fillMaxSize(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalAlignment = Alignment.Start,
                    ) {
                        DrawClock(context)
                        DrawWeather(state.weather, context)
                        DrawAlarm(context, state.alarm)
                        DrawEvents(context, state.events)
                    }
                }
                // Secret update button
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Text(
                        modifier = GlanceModifier.clickable {
                            WidgetWorkManager(context).updateNow()
                        },
                        text = "● ",
                        style = TextStyle(
                            fontSize = 30.sp,
                            color = GlanceTheme.colors.surface
                        ),
                    )
                }
            }
        }
    }
}