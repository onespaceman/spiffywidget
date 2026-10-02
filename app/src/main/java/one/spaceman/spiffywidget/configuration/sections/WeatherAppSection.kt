package one.spaceman.spiffywidget.configuration.sections

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import one.spaceman.spiffywidget.configuration.SpiffyConfigurationActivity
import one.spaceman.spiffywidget.configuration.components.NewCard
import one.spaceman.spiffywidget.configuration.components.NewDropdown

@Composable
fun WeatherAppSection(
    context: Context,
    state: SpiffyConfigurationActivity.State
) {
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
                state.setSettings(weatherApp = packageName)
            }
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