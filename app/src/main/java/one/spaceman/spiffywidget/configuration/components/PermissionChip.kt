package one.spaceman.spiffywidget.configuration.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import one.spaceman.spiffywidget.R
import one.spaceman.spiffywidget.configuration.SpiffyConfigurationActivity

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionChip(
    name: String,
    permission: PermissionState,
    state: SpiffyConfigurationActivity.State
) {
    val context = LocalContext.current
    val label = stringResource(context.packageManager.getPermissionInfo(permission.permission, 0).labelRes)
    val icon = if (permission.status.isGranted) {
        R.drawable.check_24px
    } else {
        R.drawable.close_24px
    }
    FilterChip(
        onClick = {
            if (!permission.status.isGranted) {
                if (permission.status.shouldShowRationale) {
                    state.queueDialog(
                        DialogState(
                            title = "Grant Permission",
                            text = "Spiffy widget has features that require it to $label",
                            confirmCallback = {
                                permission.launchPermissionRequest()
                                state.removeDialog()
                            },
                            dismissCallback = { state.removeDialog() }
                        )
                    )
                } else {
                    permission.launchPermissionRequest()
                }
            }
        },
        label = { Text(name) },
        modifier = Modifier.padding(horizontal = 5.dp),
        selected = permission.status.isGranted,
        leadingIcon = {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Localized description",
                modifier = Modifier.size(FilterChipDefaults.IconSize)
            )
        }
    )
}