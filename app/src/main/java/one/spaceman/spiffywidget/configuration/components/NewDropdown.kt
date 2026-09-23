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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun NewDropdown(
    initialValue: String?,
    items: Map<String, String>, // (display name, value)
    callback: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = rememberTextFieldState()
    if (!initialValue.isNullOrEmpty()) {
        selected.setTextAndPlaceCursorAtEnd(items[initialValue].orEmpty())
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            label = { Text(text = "Select Option") },
            state = selected,
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
            items.entries.forEachIndexed { index, item ->
                val (value, name) = item
                SelectableDropdownMenuItem(
                    text = { Text(text = name) },
                    selected = (selected.text == name),
                    onClick = {
                        selected.setTextAndPlaceCursorAtEnd(name)
                        callback(value)
                        expanded = false
                    },
                    shapes = MenuDefaults.itemShape(index, optionCount),
                )
            }
        }
    }
}