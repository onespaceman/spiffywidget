package one.spaceman.spiffywidget.configuration

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.configuration.sections.ColorsSection
import one.spaceman.spiffywidget.configuration.sections.PermissionsSection
import one.spaceman.spiffywidget.configuration.sections.TimezoneSection
import one.spaceman.spiffywidget.configuration.sections.WeatherAppSection
import one.spaceman.spiffywidget.state.Configuration
import one.spaceman.spiffywidget.state.SpiffyWidgetStateDefinition
import one.spaceman.spiffywidget.ui.theme.SpiffyConfigurationTheme
import one.spaceman.spiffywidget.ui.theme.WidgetColorOptions
import one.spaceman.spiffywidget.widget.SpiffyWidgetReceiver
import one.spaceman.spiffywidget.worker.WidgetWorkManager

class SpiffyConfigurationActivity : ComponentActivity() {
    @Stable
    private sealed interface ScreenState {
        data object Loading : ScreenState
        data class Success(val state: State) : ScreenState
        data class Error(val message: String) : ScreenState
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    class State(settings: Configuration) {
        var settings by mutableStateOf(settings)
            private set

        fun setSettings(
            homeTimeZone: String? = settings.homeTimeZone,
            weatherApp: String? = settings.weatherApp,
            color: WidgetColorOptions = settings.color,
            invertColors: Boolean = settings.invertColors
        ) {
            settings = settings.copy(
                homeTimeZone = homeTimeZone,
                weatherApp = weatherApp,
                color = color,
                invertColors = invertColors,
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_CANCELED, resultValue)

        val glanceAppWidgetManager = GlanceAppWidgetManager(this)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(appWidgetId)
        val context = this
        // Update widget previews
        applicationScope.launch {
            glanceAppWidgetManager.setWidgetPreviews(SpiffyWidgetReceiver::class)
        }

        enableEdgeToEdge()
        setContent {
            var screenState by remember { mutableStateOf<ScreenState>(ScreenState.Loading) }
            var state: State? = null

            LaunchedEffect(Unit) {
                val widgetState = getAppWidgetState(context, SpiffyWidgetStateDefinition, glanceId)
                state = State(widgetState.settings)
                screenState = ScreenState.Success(state)
            }

            SpiffyConfigurationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    topBar = {
                        val coroutineScope = rememberCoroutineScope()
                        TopAppBar(
                            title = { Text(text = "Spiffy Settings", style = MaterialTheme.typography.displayLarge) },
                            modifier = Modifier.padding(top = 40.dp),
                            actions = {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            // Save settings to widget state and update widget
                                            if (state != null) {
                                                val widgetState = getAppWidgetState(context, SpiffyWidgetStateDefinition, glanceId)
                                                updateAppWidgetState(
                                                    context,
                                                    SpiffyWidgetStateDefinition,
                                                    glanceId
                                                ) { widgetState.copy(settings = state.settings) }
                                                WidgetWorkManager(context).updateNow()
                                                val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                                                setResult(RESULT_OK, resultValue)
                                            }
                                            finish()
                                        }
                                    },
                                    colors = IconButtonDefaults.filledIconButtonColors(),
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.check_24px),
                                        contentDescription = "Close Button",
                                    )
                                }
                            },
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                    ) {
                        when (val s = screenState) {
                            is ScreenState.Loading -> LoadingScreen()
                            is ScreenState.Success -> Content(context, s.state)
                            is ScreenState.Error -> Text("Error")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 25.dp),
        contentAlignment = Alignment.Center
    ) {
        LoadingIndicator(
            modifier = Modifier.width(48.dp)
        )
    }
}

@Composable
fun Content(context: Context, state: SpiffyConfigurationActivity.State) {
    ColorsSection(context, state)
    PermissionsSection()
    TimezoneSection(state)
    WeatherAppSection(context, state)
}