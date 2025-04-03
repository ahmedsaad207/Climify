package com.delighted2wins.climify.data.local.preferences

import android.content.SharedPreferences
import com.delighted2wins.climify.enums.Language
import com.delighted2wins.climify.enums.LocationSource
import com.delighted2wins.climify.enums.TempUnit
import com.delighted2wins.climify.enums.WindSpeedUnit
import com.delighted2wins.climify.utils.Constants

class PreferencesDataSourceImpl(private val sharedPreferences: SharedPreferences) :
    PreferencesDataSource {

    override fun <T> saveData(value: T) {
        when (value) {
            is TempUnit -> sharedPreferences.edit().putString(Constants.KEY_TEMP_UNIT, value.name)
                .apply()

            is Language -> sharedPreferences.edit().putString(Constants.KEY_LANG, value.name)
                .apply()

            is LocationSource -> sharedPreferences.edit()
                .putString(Constants.KEY_LOCATION_SOURCE, value.name).apply()

            is WindSpeedUnit -> sharedPreferences.edit()
                .putString(Constants.KEY_WIND_SPEED_UNIT, value.name).apply()

            is Pair<*, *> -> {
                val (lat, lon) = value
                sharedPreferences.edit()
                    .putString(Constants.KEY_LAT, lat.toString())
                    .putString(Constants.KEY_LON, lon.toString()).apply()
            }
        }

    }

    override fun <T> getData(type: String): T {
        return when (type) {
            Constants.KEY_TEMP_UNIT ->
                TempUnit.valueOf(
                    sharedPreferences.getString(
                        Constants.KEY_TEMP_UNIT,
                        TempUnit.METRIC.name
                    ) ?: TempUnit.METRIC.name
                ) as T

            Constants.KEY_LANG -> Language.valueOf(
                sharedPreferences.getString(
                    Constants.KEY_LANG,
                    Language.EN.name
                ) ?: Language.EN.name
            ) as T

            Constants.KEY_LOCATION_SOURCE ->
                LocationSource.valueOf(
                    sharedPreferences.getString(
                        Constants.KEY_LOCATION_SOURCE,
                        LocationSource.GPS.name
                    )
                        ?: LocationSource.GPS.name
                ) as T

            Constants.KEY_WIND_SPEED_UNIT ->
                WindSpeedUnit.valueOf(
                    sharedPreferences.getString(
                        Constants.KEY_WIND_SPEED_UNIT,
                        WindSpeedUnit.STANDARD.name
                    )
                        ?: WindSpeedUnit.STANDARD.name
                ) as T

            else -> Pair(
                (sharedPreferences.getString(Constants.KEY_LAT, "0.0") ?: "0.0").toDouble(),
                (sharedPreferences.getString(Constants.KEY_LON, "0.0") ?: "0.0").toDouble()
            ) as T
        }
    }
}