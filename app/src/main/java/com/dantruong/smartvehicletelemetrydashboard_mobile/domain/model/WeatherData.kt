package com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model

data class WeatherData(
    val temperature: Double,
    val windSpeed: Double,
    val isDay: Boolean,
    val weatherCode: Int
)