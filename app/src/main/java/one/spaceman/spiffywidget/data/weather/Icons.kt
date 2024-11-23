package one.spaceman.spiffywidget.data.weather

import one.spaceman.spiffywidget.R

enum class WeatherIcons(
    val day: Int,
    val night: Int,
    val description: String
) {
    CODE_0(R.drawable.clear, R.drawable.clear_night, "CLEAR"),
    CODE_1(R.drawable.clear, R.drawable.clear_night, "MOSTLY_CLEAR"),
    CODE_2(R.drawable.partly_cloudy, R.drawable.partly_cloudy_night, "PARTLY_CLOUDY"),
    CODE_3(R.drawable.cloudy, R.drawable.cloudy, "OVERCAST"),
    CODE_45(R.drawable.fog, R.drawable.fog_night, "FOG"),
    CODE_48(R.drawable.fog, R.drawable.fog_night, "FOG"),
    CODE_51(R.drawable.light_rain, R.drawable.light_rain, "LIGHT_DRIZZLE"),
    CODE_53(R.drawable.moderate_rain, R.drawable.moderate_rain, "DRIZZLE"),
    CODE_55(R.drawable.heavy_rain, R.drawable.heavy_rain, "HEAVY_DRIZZLE"),
    CODE_56(R.drawable.light_snow, R.drawable.light_snow, "LIGHT_FREEZING_DRIZZLE"),
    CODE_57(R.drawable.rainstorm, R.drawable.rainstorm, "HEAVY_FREEZING_DRIZZLE"),
    CODE_61(R.drawable.light_rain, R.drawable.light_rain, "LIGHT_RAIN"),
    CODE_63(R.drawable.moderate_rain, R.drawable.moderate_rain, "RAIN"),
    CODE_65(R.drawable.heavy_rain, R.drawable.heavy_rain, "HEAVY_RAIN"),
    CODE_66(R.drawable.light_rain, R.drawable.light_rain, "LIGHT_FREEZING_RAIN"),
    CODE_67(R.drawable.heavy_rain, R.drawable.heavy_rain, "HEAVY_FREEZING_RAIN"),
    CODE_71(R.drawable.light_snow, R.drawable.light_snow, "LIGHT_SNOW"),
    CODE_73(R.drawable.moderate_snow, R.drawable.moderate_snow, "SNOW"),
    CODE_75(R.drawable.heavy_snow, R.drawable.heavy_snow, "HEAVY_SNOW"),
    CODE_77(R.drawable.moderate_snow, R.drawable.moderate_snow, "SNOW"),
    CODE_80(R.drawable.showers, R.drawable.showers_night, "LIGHT_SHOWERS"),
    CODE_81(R.drawable.showers, R.drawable.showers_night, "SHOWERS"),
    CODE_82(R.drawable.showers, R.drawable.showers_night, "HEAVY_SHOWERS"),
    CODE_85(R.drawable.snow_showers, R.drawable.snow_showers_night, "LIGHT_SNOW_SHOWERS"),
    CODE_86(R.drawable.snow_showers, R.drawable.snow_showers_night, "HEAVY_SNOW_SHOWERS"),
    CODE_95(R.drawable.thunderstorm, R.drawable.thunderstorm, "THUNDERSTORMS"),
    CODE_96(R.drawable.heavy_snow, R.drawable.heavy_snow, "LIGHT_HAIL"),
    CODE_99(R.drawable.heavy_snow, R.drawable.heavy_snow, "HEAVY_HAIL");

    companion object {
        fun getIcon(code: Int, isDay: Int): Int {
            val icon = if (intToBool(isDay)) {
                WeatherIcons.valueOf("CODE_${code}").day
            } else {
                WeatherIcons.valueOf("CODE_${code}").night
            }
            return icon
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