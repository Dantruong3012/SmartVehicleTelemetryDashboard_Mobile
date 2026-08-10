package com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert_logs")
data class AlertLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,
    val message: String,
    val speed: Int,
    val batteryLevel: Int,
    val engineTemperature: Int,
    val createdAt: Long = System.currentTimeMillis()
)
