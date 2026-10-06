package one.spaceman.spiffywidget.configuration.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
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
    NewCard(
        "Home Timezone",
        "When traveling, show the current and home times"
    ) {
        val zones = rememberSaveable { getTimeZones() }

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