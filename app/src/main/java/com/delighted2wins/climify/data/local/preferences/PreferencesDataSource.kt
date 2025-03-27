package com.delighted2wins.climify.data.local.preferences

interface PreferencesDataSource {

    fun <T> saveData(value: T)

    fun <T> getData(type: String): T

}