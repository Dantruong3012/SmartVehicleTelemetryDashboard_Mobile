package com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository

import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.WeatherData

interface WeatherRepository {
    suspend fun getWeatherData(): Result<WeatherData>
    suspend fun getCachedWeatherData(): WeatherData?
}
