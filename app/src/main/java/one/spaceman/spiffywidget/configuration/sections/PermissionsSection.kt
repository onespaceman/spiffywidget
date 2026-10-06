package one.spaceman.spiffywidget.configuration.sections

import android.Manifest
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import one.spaceman.spiffywidget.configuration.components.NewCard
import one.spaceman.spiffywidget.configuration.components.PermissionChip

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun PermissionsSection() {
    NewCard(
        "Permissions",
        "Some features will not work without these permissions"
    ) {

        val locationPermissionState = rememberMultiplePermissionsState(listOf(Manifest.permission.ACCESS_COARSE_LOCATION))
        val bgLocationPermissionState = rememberMultiplePermissionsState(listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
        val calendarPermissionState = rememberMultiplePermissionsState(listOf(Manifest.permission.READ_CALENDAR))

        FlowRow {
            PermissionChip("Calendar", calendarPermissionState)
            PermissionChip("Location", locationPermissionState)
            PermissionChip("BG Location", bgLocationPermissionState, locationPermissionState.allPermissionsGranted)
        }
    }
}