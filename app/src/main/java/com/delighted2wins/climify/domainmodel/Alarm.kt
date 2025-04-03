package com.delighted2wins.climify.domainmodel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey
    val tag: String,
    val startDuration: Long,
    val endDuration: Long,
    var type: String,
    var isChecked: Boolean = true
)