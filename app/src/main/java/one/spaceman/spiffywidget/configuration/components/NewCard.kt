package one.spaceman.spiffywidget.configuration.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NewCard(
    title: String? = null,
    description: String? = null,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}