package one.spaceman.spiffywidget.configuration.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import one.spaceman.spiffywidget.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionChip(
    name: String,
    permission: MultiplePermissionsState,
) {
    val icon = if (permission.allPermissionsGranted) {
        R.drawable.check_24px
    } else {
        R.drawable.close_24px
    }
    FilterChip(
        onClick = {
            if (!permission.allPermissionsGranted) {
                permission.launchMultiplePermissionRequest()
            }
        },
        label = { Text(name) },
        modifier = Modifier.padding(horizontal = 5.dp),
        selected = permission.allPermissionsGranted,
        leadingIcon = {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Localized description",
                modifier = Modifier.size(FilterChipDefaults.IconSize)
            )
        }
    )
}