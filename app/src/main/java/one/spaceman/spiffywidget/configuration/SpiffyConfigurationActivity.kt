package one.spaceman.spiffywidget.configuration

import android.Manifest
import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.configuration.components.DialogState
import one.spaceman.spiffywidget.configuration.components.NewCard
import one.spaceman.spiffywidget.configuration.components.NewDialog
import one.spaceman.spiffywidget.configuration.components.NewDropdown
import one.spaceman.spiffywidget.configuration.components.PermissionChip
import one.spaceman.spiffywidget.state.Configuration
import one.spaceman.spiffywidget.state.SpiffyWidgetStateDefinition
import one.spaceman.spiffywidget.ui.theme.SpiffyWidgetTheme
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

        fun setHomeTimeZone(timeZone: String) {
            settings = settings.copy(homeTimeZone = timeZone)
        }

        fun setWeatherApp(weatherApp: String) {
            settings = settings.copy(weatherApp = weatherApp)
        }

        fun setInvertColors(value: Boolean) {
            settings = settings.copy(invertColors = value)
        }

        val dialogs = mutableStateListOf<DialogState>()
        fun queueDialog(dialog: DialogState) {
            dialogs.add(dialog)
        }

        fun removeDialog() {
            dialogs.remove(dialogs.first())
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
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

            SpiffyWidgetTheme {
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
                                                WidgetWorkManager(context).scheduleUpdate()
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

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 25.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(48.dp),
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Content(context: Context, state: SpiffyConfigurationActivity.State) {
    val state by remember { mutableStateOf(state) }
    // Dialog
    if (state.dialogs.isNotEmpty()) {
        NewDialog(state.dialogs.first())
    }

    // Permissions
    NewCard {
        // background location needs to be asked by itself, so no point in using rememberMultiplePermissionsState
        val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_COARSE_LOCATION)
        val bgLocationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        val calendarPermissionState = rememberPermissionState(Manifest.permission.READ_CALENDAR)
        val bluetoothPermissionState = rememberPermissionState(Manifest.permission.BLUETOOTH_CONNECT)
        Text(
            text = "Grant permissions",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = "Some features will not work without these permissions",
        )
        Spacer(Modifier.height(10.dp))
        FlowRow {
            if (!locationPermissionState.status.isGranted ||
                (locationPermissionState.status.isGranted && bgLocationPermissionState.status.isGranted)
            ) {
                PermissionChip("Location", locationPermissionState, state)
            } else {
                PermissionChip("Background Location", bgLocationPermissionState, state)
            }
            PermissionChip("Calendar", calendarPermissionState, state)
            PermissionChip("Bluetooth", bluetoothPermissionState, state)
        }
    }

    // Timezone
    NewCard {
        val zones = rememberSaveable { TimeZone.availableZoneIds.toSortedSet() }

        Text(
            text = "Set Home Timezone",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Text(
            text = "When traveling, Spiffy Widget will show current and home times",
        )
        Spacer(Modifier.height(10.dp))
        NewDropdown(
            state.settings.homeTimeZone,
            zones
        ) { selected ->
            state.setHomeTimeZone(selected)
        }
    }

    // Weather app
    NewCard {
        val apps = getAppList(context.packageManager)

        Text(
            text = "Set Weather App",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Text(
            text = "Which app to launch when clicking on the weather",
        )
        NewDropdown(
            apps.filterValues { it == state.settings.weatherApp }.keys.firstOrNull(),
            apps.keys.toSortedSet()
        ) { selected ->
            val packageName = apps[selected]
            if (!packageName.isNullOrEmpty()) {
                state.setWeatherApp(packageName)
            }
        }
    }

    NewCard {
        Text(
            text = "Invert Colors",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Invert text colors in the widget",
            )

            var checked by remember { mutableStateOf(state.settings.invertColors) }
            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = !checked
                    state.setInvertColors(checked)
                }
            )

        }
    }
}

@SuppressLint("QueryPermissionsNeeded")
fun getAppList(packageManager: PackageManager): Map<String, String> {
    // Retrieve all installed applications
    val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

    val appList = installedApps
        .filter { it.flags and ApplicationInfo.FLAG_SYSTEM != 1 || it.packageName.contains("weather") }
        .associate { it.loadLabel(packageManager).toString() to it.packageName }

    return appList
}