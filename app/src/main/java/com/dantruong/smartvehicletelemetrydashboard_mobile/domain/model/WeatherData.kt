package com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model

data class WeatherData(
    val temperature: Double,
    val windSpeed: Double,
    val isDay: Boolean,
    val weatherCode: Int
)

data class WeatherForecast(
    val hourly: List<HourlyWeatherForecast>,
    val daily: List<DailyWeatherForecast>
)

data class HourlyWeatherForecast(
    val time: String,
    val temperature: Double,
    val windSpeed: Double,
    val weatherCode: Int
)

data class DailyWeatherForecast(
    val date: String,
    val minTemperature: Double,
    val maxTemperature: Double,
    val weatherCode: Int
)
