package one.spaceman.spiffywidget.configuration.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun NewDropdown(
    initialValue: String?,
    items: Set<String>,
    callback: (selected: String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val textFieldState = rememberTextFieldState(initialValue.orEmpty())
    var checkedIndex by rememberSaveable { mutableIntStateOf(0) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            label = { Text(text = "Select Option") },
            state = textFieldState,
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            lineLimits = TextFieldLineLimits.SingleLine,
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MenuDefaults.groupStandardContainerColor
        ) {
            val optionCount = items.size
            items.forEachIndexed { index, item ->
                SelectableDropdownMenuItem(
                    text = { Text(text = item) },
                    selected = index == checkedIndex,
                    onClick = {
                        textFieldState.setTextAndPlaceCursorAtEnd(item)
                        expanded = false
                        callback(item)
                    },
                    shapes = MenuDefaults.itemShape(index, optionCount),
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}