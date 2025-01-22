package one.spaceman.spiffywidget.data.weather

import one.spaceman.spiffywidget.R.drawable as i

enum class WeatherIcons(
    val description: String,
    val icons: List<Int>,
    val text: String
) {
    CODE_0("CLEAR", listOf(), "Clear Skies"),
    CODE_1("MOSTLY_CLEAR", listOf(), "Mostly Clear"),
    CODE_2("PARTLY_CLOUDY", listOf(i.cloudy), "Partly Cloudy"),
    CODE_3("OVERCAST", listOf(i.overcast), "Overcast"),
    CODE_45("FOG", listOf(i.partly_cloudy, i.fog), "Foggy"),
    CODE_48("RIME_FOG", listOf(i.partly_cloudy, i.fog), "Foggy"),
    CODE_51("LIGHT_DRIZZLE", listOf(i.partly_cloudy, i.drizzle), "Light Drizzle"),
    CODE_53("DRIZZLE", listOf(i.cloudy, i.drizzle), "Drizzle"),
    CODE_55("HEAVY_DRIZZLE", listOf(i.overcast, i.drizzle), "Heavy Drizzle"),
    CODE_56("LIGHT_FREEZING_DRIZZLE", listOf(i.cloudy, i.freezing_rain), "Freezing Drizzle"),
    CODE_57("HEAVY_FREEZING_DRIZZLE", listOf(i.overcast, i.freezing_rain), "Heavy Freezing Drizzle"),
    CODE_61("LIGHT_RAIN", listOf(i.partly_cloudy, i.rain), "Light Rain"),
    CODE_63("RAIN", listOf(i.cloudy, i.rain), "Rainy"),
    CODE_65("HEAVY_RAIN", listOf(i.overcast, i.rain), "Heavy Rain"),
    CODE_66("LIGHT_FREEZING_RAIN", listOf(i.cloudy, i.freezing_rain), "Freezing Rain"),
    CODE_67("HEAVY_FREEZING_RAIN", listOf(i.overcast, i.freezing_rain), "Heavy Freezing Rain"),
    CODE_71("LIGHT_SNOW", listOf(i.partly_cloudy, i.snow), "Light Snow"),
    CODE_73("SNOW", listOf(i.cloudy, i.snow), "Snowy"),
    CODE_75("HEAVY_SNOW", listOf(i.overcast, i.snow), "Heavy Snow"),
    CODE_77("SNOW", listOf(i.partly_cloudy, i.snow), "Snowy"),
    CODE_80("LIGHT_SHOWERS", listOf(i.partly_cloudy, i.rain), "Light Showers"),
    CODE_81("SHOWERS", listOf(i.cloudy, i.rain), "Showers"),
    CODE_82("HEAVY_SHOWERS", listOf(i.overcast, i.rain), "Heavy Showers"),
    CODE_85("LIGHT_SNOW_SHOWERS", listOf(i.cloudy, i.snow), "Snowy"),
    CODE_86("HEAVY_SNOW_SHOWERS", listOf(i.overcast, i.snow), "Heavy Snow"),
    CODE_95("THUNDERSTORMS", listOf(i.overcast, i.thunderstorms), "Thunderstorms"),
    CODE_96("LIGHT_HAIL", listOf(i.cloudy, i.hail), "Hail"),
    CODE_99("HEAVY_HAIL", listOf(i.overcast, i.hail), "Heavy Hail");

    companion object {
        fun getIcon(code: Int, isDay: Int): List<Int> {
            return if (intToBool(isDay)) {
                val icon = valueOf("CODE_${code}").icons
                if (icon.isEmpty()) {
                    listOf(i.day_full)
                } else {
                    listOf(i.day_small) + icon
                }
            } else {
                val icon = valueOf("CODE_${code}").icons
                if (icon.isEmpty()) {
                    listOf(i.night_full)
                } else {
                    listOf(i.night_small) + icon
                }
            }
        }

        fun getDescription(code: Int, isDay: Int): String {
            var description = WeatherIcons.valueOf("CODE_${code}").description
            description += if (intToBool(isDay)) {
                "_DAY"
            } else {
                "_NIGHT"
            }
            return description
        }

        fun getText(code: Int): String {
            return WeatherIcons.valueOf("CODE_${code}").text
        }

        private fun intToBool(int: Int): Boolean {
            return (int != 0)
        }
    }
}