package one.spaceman.spiffywidget.configuration.sections

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import one.spaceman.spiffywidget.configuration.SpiffyConfigurationActivity
import one.spaceman.spiffywidget.configuration.components.NewCard
import one.spaceman.spiffywidget.configuration.components.NewDropdown
import one.spaceman.spiffywidget.state.Configuration

@Preview
@Composable
fun TimezoneSection(
    state: SpiffyConfigurationActivity.State = SpiffyConfigurationActivity.State(settings = Configuration())
) {
    NewCard {
        val zones = rememberSaveable { getTimeZones() }

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
            state.setSettings(homeTimeZone = selected)
        }
    }
}

internal fun getTimeZones(): Set<String> {
    return TimeZone.availableZoneIds
        .filter { it.contains("/") }
        .toSortedSet()
}