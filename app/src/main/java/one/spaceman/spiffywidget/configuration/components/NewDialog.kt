package one.spaceman.spiffywidget.configuration.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi

data class DialogState(
    val title: String,
    val text: String,
    val confirmCallback: () -> Unit,
    var dismissCallback: () -> Unit
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NewDialog(s: DialogState) {
//    val context = LocalContext.current
//    val permissionLabel = stringResource(
//        context.packageManager.getPermissionInfo(permissionState.permission, 0).labelRes
//    )

    AlertDialog(
        onDismissRequest = { s.dismissCallback() },
        title = { Text(text = s.title) },
        text = { Text(text = s.text) },
        confirmButton = {
            Button(
                onClick = { s.confirmCallback() }
            ) {
                Text(text = "Grant")
            }
        },
        dismissButton = {
            Button(
                onClick = { s.dismissCallback() }
            ) {
                Text(text = "Cancel")
            }
        }
    )
}