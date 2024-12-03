package one.spaceman.spiffywidget.data.weather

import one.spaceman.spiffywidget.R.drawable as i

enum class WeatherIcons(
    val description: String,
    val icons: List<Int>
) {
    CODE_0("CLEAR", listOf()),
    CODE_1("MOSTLY_CLEAR", listOf()),
    CODE_2("PARTLY_CLOUDY", listOf(i.cloudy)),
    CODE_3("OVERCAST", listOf(i.overcast)),
    CODE_45("FOG", listOf(i.partly_cloudy, i.fog)),
    CODE_48("RIME_FOG", listOf(i.partly_cloudy, i.fog)),
    CODE_51("LIGHT_DRIZZLE", listOf(i.partly_cloudy, i.drizzle)),
    CODE_53("DRIZZLE", listOf(i.cloudy, i.drizzle)),
    CODE_55("HEAVY_DRIZZLE", listOf(i.overcast, i.drizzle)),
    CODE_56("LIGHT_FREEZING_DRIZZLE", listOf(i.cloudy, i.freezing_rain)),
    CODE_57("HEAVY_FREEZING_DRIZZLE", listOf(i.overcast, i.freezing_rain)),
    CODE_61("LIGHT_RAIN", listOf(i.partly_cloudy, i.rain)),
    CODE_63("RAIN", listOf(i.cloudy, i.rain)),
    CODE_65("HEAVY_RAIN", listOf(i.overcast, i.rain)),
    CODE_66("LIGHT_FREEZING_RAIN", listOf(i.cloudy, i.freezing_rain)),
    CODE_67("HEAVY_FREEZING_RAIN", listOf(i.overcast, i.freezing_rain)),
    CODE_71("LIGHT_SNOW", listOf(i.partly_cloudy, i.snow)),
    CODE_73("SNOW", listOf(i.cloudy, i.snow)),
    CODE_75("HEAVY_SNOW", listOf(i.overcast, i.snow)),
    CODE_77("SNOW", listOf(i.partly_cloudy, i.snow)),
    CODE_80("LIGHT_SHOWERS", listOf(i.partly_cloudy, i.rain)),
    CODE_81("SHOWERS", listOf(i.cloudy, i.rain)),
    CODE_82("HEAVY_SHOWERS", listOf(i.overcast, i.rain)),
    CODE_85("LIGHT_SNOW_SHOWERS", listOf(i.cloudy, i.snow)),
    CODE_86("HEAVY_SNOW_SHOWERS", listOf(i.overcast, i.snow)),
    CODE_95("THUNDERSTORMS", listOf(i.overcast, i.thunderstorms)),
    CODE_96("LIGHT_HAIL", listOf(i.cloudy, i.hail)),
    CODE_99("HEAVY_HAIL", listOf(i.overcast, i.hail));

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

        private fun intToBool(int: Int): Boolean {
            return (int != 0)
        }
    }
}