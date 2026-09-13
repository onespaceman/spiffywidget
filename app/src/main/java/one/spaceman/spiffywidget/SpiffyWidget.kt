package one.spaceman.spiffywidget

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
import androidx.glance.background
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
import one.spaceman.spiffywidget.components.DrawCalendar
import one.spaceman.spiffywidget.components.DrawClock
import one.spaceman.spiffywidget.components.DrawWeather
import one.spaceman.spiffywidget.state.SpiffyWidgetState
import one.spaceman.spiffywidget.state.SpiffyWidgetStateDefinition
import one.spaceman.spiffywidget.worker.WidgetWorkManager
import one.spaceman.spiffywidget.worker.WidgetWorkManager.PartialUpdate

class SpiffyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SpiffyWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            Intent.ACTION_LOCALE_CHANGED, Intent.ACTION_TIMEZONE_CHANGED, Intent.ACTION_BOOT_COMPLETED -> {
                WidgetWorkManager(context).updateNow()
            }

            AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED -> {
                WidgetWorkManager(context).updateNow(arrayOf(PartialUpdate.ALARM))
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
                Box(
                    modifier = GlanceModifier.fillMaxSize().padding(vertical = 2.dp),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Column(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .background(GlanceTheme.colors.primaryContainer.getColor(context).copy(alpha = 0.6f))
                            .padding(vertical = 15.dp, horizontal = 10.dp)
                            .cornerRadius(15.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalAlignment = Alignment.Start,
                    ) {
                        DrawClock(context, state.alarm)
                        DrawCalendar(context, state.events)
                        DrawWeather(context, state.weather)
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