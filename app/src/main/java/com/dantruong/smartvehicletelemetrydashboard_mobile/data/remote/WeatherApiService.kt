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
}