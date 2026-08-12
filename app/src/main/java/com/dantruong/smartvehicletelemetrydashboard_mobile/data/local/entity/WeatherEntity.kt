package com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val id: Int = 1, 
    val temperature: Double,
    val windSpeed: Double,
    val isDay: Boolean,
    val weatherCode: Int
)
