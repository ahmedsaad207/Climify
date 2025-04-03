package com.delighted2wins.climify.data.local.db

import com.delighted2wins.climify.domainmodel.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmsLocalDataSource {
    suspend fun insertAlarm(alarm: Alarm): Long

    fun getAllAlarms(): Flow<List<Alarm>>

    suspend fun deleteAlarm(alarm: Alarm): Int
}