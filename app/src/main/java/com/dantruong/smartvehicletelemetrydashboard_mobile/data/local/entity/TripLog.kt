package com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip_logs")
data class TripLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val speed: Int,
    val batteryLevel: Int,
    val engineTemperature: Int,
    val loggedAt: Long = System.currentTimeMillis()
)
