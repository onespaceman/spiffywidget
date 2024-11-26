package one.spaceman.spiffywidget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import one.spaceman.spiffywidget.components.DrawAlarm
import one.spaceman.spiffywidget.components.DrawClock
import one.spaceman.spiffywidget.components.DrawEvents
import one.spaceman.spiffywidget.components.DrawWeather
import one.spaceman.spiffywidget.state.SpiffyWidgetState
import one.spaceman.spiffywidget.state.SpiffyWidgetStateDefinition
import one.spaceman.spiffywidget.worker.BroadcastReceiverManager
import one.spaceman.spiffywidget.worker.WidgetWorkManager

class SpiffyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SpiffyWidget()

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
        BroadcastReceiverManager.register(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WidgetWorkManager(context).cancel()
        BroadcastReceiverManager.unregister(context)
    }
}

class SpiffyWidget : GlanceAppWidget() {

    override val stateDefinition = SpiffyWidgetStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {
            val state = currentState<SpiffyWidgetState>()

            Box(GlanceModifier.fillMaxSize()) {
                Column(
                    modifier = GlanceModifier.fillMaxSize(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    DrawClock(context)
                    DrawAlarm(context, state.alarm)
                    DrawWeather(state.weather, state.location)
                    DrawEvents(context, state.events)
                }
            }
            // Secret update button
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    modifier = GlanceModifier.clickable{
                        WidgetWorkManager(context).updateNow()
                    },
                    text = "● ",
                    style = TextStyle(
                        fontSize = 30.sp,
                        color = ColorProvider(Color(0x0FFFFFFF))
                    ),
                )
            }
        }
    }
}