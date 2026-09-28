package one.spaceman.spiffywidget.widget

import android.app.AlarmManager
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.wrapContentHeight
import androidx.glance.material3.ColorProviders
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import kotlinx.datetime.LocalDateTime
import one.spaceman.spiffywidget.data.getWallpaper
import one.spaceman.spiffywidget.data.weather.WeatherCodes
import one.spaceman.spiffywidget.state.CalendarEvent
import one.spaceman.spiffywidget.state.SpiffyWidgetState
import one.spaceman.spiffywidget.state.SpiffyWidgetStateDefinition
import one.spaceman.spiffywidget.state.Weather
import one.spaceman.spiffywidget.ui.theme.hidden
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

//            BluetoothDevice.ACTION_ACL_CONNECTED -> {
//                val device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
//                val serviceIntent = Intent(context, BluetoothService::class.java).apply {
//                    putExtra(BluetoothDevice.EXTRA_DEVICE, device)
//                }
//                ServiceCompat.startForeground(context, 100,ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE)
//                ContextCompat.startForegroundService(context, serviceIntent)
//            }
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

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val state = currentState<SpiffyWidgetState>()
            val dark = dynamicDarkColorScheme(context)
            val light = dynamicLightColorScheme(context)
            val colors = if (state.settings.invertColors) ColorProviders(light = dark, dark = light) else ColorProviders(light = light, dark = dark)
            GlanceTheme(colors = colors) {
                Content(context, state)
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
        val wallpaper = getWallpaper(context)
        provideContent {
            GlanceTheme {
                Image(
                    provider = ImageProvider(wallpaper),
                    contentDescription = "Background wallpaper",
                    contentScale = ContentScale.Crop,
                    modifier = GlanceModifier.fillMaxSize(),
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
            DrawAlarm(context, state.alarm)
            DrawWeather(context, state.weather, state.settings.weatherApp)
            DrawClock(context, state.settings.homeTimeZone)
            DrawCalendar(context, state.events)
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
                fontSize = 30.sp, color = GlanceTheme.colors.hidden
            ),
        )
    }
}