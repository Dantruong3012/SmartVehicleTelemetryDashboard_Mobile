package com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "temperature")
data class Temperature(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val currentTemp: Int,
    val timeChange: Long = System.currentTimeMillis()
)