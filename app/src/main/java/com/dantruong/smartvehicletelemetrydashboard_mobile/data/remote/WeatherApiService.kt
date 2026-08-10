package com.dantruong.smartvehicletelemetrydashboard_mobile.data.remote

import com.dantruong.smartvehicletelemetrydashboard_mobile.data.remote.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double = 21.0285,
        @Query("longitude") longitude: Double = 105.8542,
        @Query("current_weather") currentWeather: Boolean = true
    ): WeatherResponse

    @GET("v1/forecast")
    suspend fun getWeatherForecast(
        @Query("latitude") latitude: Double = 21.0285,
        @Query("longitude") longitude: Double = 105.8542,
        @Query("hourly") hourly: String = "temperature_2m,weather_code,wind_speed_10m",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min",
        @Query("forecast_days") forecastDays: Int = 7,
        @Query("timezone") timezone: String = "Asia/Bangkok"
    ): WeatherResponse
}
