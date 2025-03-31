package com.delighted2wins.climify.data.local.db

import com.delighted2wins.climify.domainmodel.Alarm
import kotlinx.coroutines.flow.Flow

class AlarmsLocalDataSourceImpl(private val dao: WeatherDao) : AlarmsLocalDataSource {
    override suspend fun insertAlarm(alarm: Alarm): Long {
        return dao.insertAlarm(alarm)
    }

    override fun getAllAlarms(): Flow<List<Alarm>> {
        return dao.getAllAlarms()
    }

    override suspend fun deleteAlarm(alarm: Alarm): Int {
        return dao.deleteAlarm(alarm)
    }
}