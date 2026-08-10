package com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val id: Int = 1, // Luôn ép ID = 1 để ghi đè dữ liệu cũ
    val temperature: Double,
    val windSpeed: Double,
    val isDay: Boolean,
    val weatherCode: Int
)
