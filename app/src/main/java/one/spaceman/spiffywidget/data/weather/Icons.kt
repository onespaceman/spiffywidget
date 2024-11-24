package one.spaceman.spiffywidget.data.weather

import one.spaceman.spiffywidget.R

enum class WeatherIcons(
    val day: Int,
    val night: Int,
    val description: String
) {
    CODE_0(R.drawable.cloudy_day, R.drawable.clear_night, "CLEAR"),
    CODE_1(R.drawable.cloudy_day, R.drawable.clear_night, "MOSTLY_CLEAR"),
    CODE_2(R.drawable.cloudy_day, R.drawable.cloudy_night, "PARTLY_CLOUDY"),
    CODE_3(R.drawable.overcast_day, R.drawable.overcast_night, "OVERCAST"),
    CODE_45(R.drawable.fog_day, R.drawable.fog_night, "FOG"),
    CODE_48(R.drawable.fog_day, R.drawable.fog_night, "RIME_FOG"),
    CODE_51(R.drawable.drizzle_light_day, R.drawable.drizzle_light_night, "LIGHT_DRIZZLE"),
    CODE_53(R.drawable.drizzle_moderate_day, R.drawable.drizzle_moderate_night, "DRIZZLE"),
    CODE_55(R.drawable.drizzle_heavy, R.drawable.drizzle_heavy, "HEAVY_DRIZZLE"),
    CODE_56(R.drawable.freezing_rain_moderate_day, R.drawable.freezing_rain_moderate_night, "LIGHT_FREEZING_DRIZZLE"),
    CODE_57(R.drawable.freezing_rain_heavy, R.drawable.freezing_rain_heavy, "HEAVY_FREEZING_DRIZZLE"),
    CODE_61(R.drawable.rain_light_day, R.drawable.rain_light_night, "LIGHT_RAIN"),
    CODE_63(R.drawable.rain_moderate_day, R.drawable.rain_moderate_night, "RAIN"),
    CODE_65(R.drawable.rain_heavy, R.drawable.rain_heavy, "HEAVY_RAIN"),
    CODE_66(R.drawable.freezing_rain_moderate_day, R.drawable.freezing_rain_moderate_night, "LIGHT_FREEZING_RAIN"),
    CODE_67(R.drawable.freezing_rain_heavy, R.drawable.freezing_rain_heavy, "HEAVY_FREEZING_RAIN"),
    CODE_71(R.drawable.snow_light_day, R.drawable.snow_light_night, "LIGHT_SNOW"),
    CODE_73(R.drawable.snow_moderate_day, R.drawable.snow_moderate_night, "SNOW"),
    CODE_75(R.drawable.snow_heavy, R.drawable.snow_heavy, "HEAVY_SNOW"),
    CODE_77(R.drawable.snow_moderate_day, R.drawable.snow_moderate_night, "SNOW"),
    CODE_80(R.drawable.rain_light_day, R.drawable.rain_light_night, "LIGHT_SHOWERS"),
    CODE_81(R.drawable.rain_moderate_day, R.drawable.rain_moderate_night, "SHOWERS"),
    CODE_82(R.drawable.rain_heavy, R.drawable.rain_heavy, "HEAVY_SHOWERS"),
    CODE_85(R.drawable.snow_moderate_day, R.drawable.snow_moderate_night, "LIGHT_SNOW_SHOWERS"),
    CODE_86(R.drawable.snow_heavy, R.drawable.snow_heavy, "HEAVY_SNOW_SHOWERS"),
    CODE_95(R.drawable.thunderstorms, R.drawable.thunderstorms, "THUNDERSTORMS"),
    CODE_96(R.drawable.hail_light_day, R.drawable.hail_light_night, "LIGHT_HAIL"),
    CODE_99(R.drawable.hail_heavy, R.drawable.hail_heavy, "HEAVY_HAIL");

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