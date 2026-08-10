package com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository

import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.WeatherData
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.WeatherForecast

interface WeatherRepository {
    suspend fun getWeatherData(): Result<WeatherData>
    suspend fun getWeatherForecast(): Result<WeatherForecast>
    suspend fun getCachedWeatherData(): WeatherData?
}
