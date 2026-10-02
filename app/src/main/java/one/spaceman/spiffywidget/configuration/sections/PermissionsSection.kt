package one.spaceman.spiffywidget.configuration.sections

import android.Manifest
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import one.spaceman.spiffywidget.configuration.components.NewCard
import one.spaceman.spiffywidget.configuration.components.PermissionChip

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun PermissionsSection() {
    NewCard {

        val locationPermissionState = rememberMultiplePermissionsState(listOf(Manifest.permission.ACCESS_COARSE_LOCATION))
        val bgLocationPermissionState = rememberMultiplePermissionsState(listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
        val calendarPermissionState = rememberMultiplePermissionsState(listOf(Manifest.permission.READ_CALENDAR))
        val bluetoothPermissionState = rememberMultiplePermissionsState(
            listOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
        )

        Text(
            text = "Grant permissions",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = "Some features will not work without these permissions",
        )
        Spacer(Modifier.height(10.dp))
        FlowRow {
            // background location needs to be requested by itself, after regular location is granted
            if (!locationPermissionState.allPermissionsGranted ||
                (locationPermissionState.allPermissionsGranted && bgLocationPermissionState.allPermissionsGranted)
            ) {
                PermissionChip("Location", locationPermissionState)
            } else {
                PermissionChip("Background Location", bgLocationPermissionState)
            }
            PermissionChip("Calendar", calendarPermissionState)
            PermissionChip("Bluetooth", bluetoothPermissionState)
        }
    }
}