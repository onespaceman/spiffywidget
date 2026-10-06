package one.spaceman.spiffywidget.widget

import android.app.AlarmManager
import android.app.WallpaperManager
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import kotlinx.datetime.LocalDateTime
import one.spaceman.spiffywidget.data.weather.WeatherCodes
import one.spaceman.spiffywidget.state.CalendarEvent
import one.spaceman.spiffywidget.state.SpiffyWidgetState
import one.spaceman.spiffywidget.state.SpiffyWidgetStateDefinition
import one.spaceman.spiffywidget.state.Weather
import one.spaceman.spiffywidget.ui.theme.SpiffyWidgetColors
import one.spaceman.spiffywidget.ui.theme.getColorProviders
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
            Intent.ACTION_LOCALE_CHANGED, Intent.ACTION_TIMEZONE_CHANGED, Intent.ACTION_BOOT_COMPLETED ->
                WidgetWorkManager(context).updateNow()

            AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED ->
                WidgetWorkManager(context).updateNow(arrayOf(WidgetWorkManager.PartialUpdate.ALARM))
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
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

    companion object {
        val LARGE = DpSize(400.dp, 400.dp)
        val MEDIUM = DpSize(300.dp, 400.dp)
        val SMALL = DpSize(200.dp, 400.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(
            LARGE,
            MEDIUM,
            SMALL
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val state = currentState<SpiffyWidgetState>()
            GlanceTheme {
                val (fgColor, bgColor) = state.settings.color.getColorProviders(state.settings.invertColors)
                SpiffyWidgetColors(fgColor, bgColor) {
                    Content(context, state)
                }
            }
        }
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        val state = SpiffyWidgetState(
            alarm = "9:00am",
            events = listOf(
                CalendarEvent(
                    id = 1,
                    title = "Talk Like A Pirate Day",
                    allDay = true,
                    color = -15619228,
                    start = LocalDateTime(3000, 9, 19, 0, 0),
                    end = LocalDateTime(3000, 9, 19, 23, 59),
                    onDays = listOf(19),
                    dateString = "Sep 19",
                )
            ),
            weather = Weather(
                temperature = 58,
                temperatureLow = 48,
                temperatureHigh = 65,
                uvIndex = 5,
                code = WeatherCodes.OVERCAST,
                extra = "Sunset at 7:15ᴘᴍ",
                location = "Easter Island"
            )
        )
        provideContent {
            GlanceTheme {
                val wallpaper = WallpaperManager.getInstance(context).builtInDrawable.toBitmap()
                Image(
                    provider = ImageProvider(wallpaper),
                    contentDescription = "Wallpaper",
                    modifier = GlanceModifier.fillMaxWidth()
                )
                Content(context, state)
            }
        }
    }
}

@Composable
fun Content(context: Context, state: SpiffyWidgetState) {
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
            Row(
                modifier = GlanceModifier.fillMaxWidth().padding(bottom = 15.dp),
                horizontalAlignment = Alignment.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DrawAlarm(context, state.alarm)
                DrawClock(context, state.settings.homeTimeZone)
            }
            DrawWeather(context, state.weather, state.settings.weatherApp)
            DrawCalendar(context, state.events)
        }
    }
    // Secret update button
    Box(
        modifier = GlanceModifier
            .height(30.dp)
            .fillMaxWidth()
            .clickable { WidgetWorkManager(context).updateNow() },
        contentAlignment = Alignment.TopCenter
    ) { }
}