package com.dantruong.smartvehicletelemetrydashboard_mobile.data.remote.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("current_weather")
    val currentWeather: CurrentWeatherDto? = null
)

data class CurrentWeatherDto(
    @SerializedName("temperature")
    val temperature: Double = 0.0,

    @SerializedName("windspeed")
    val windspeed: Double = 0.0,

    @SerializedName("is_day")
    val isDay: Int = 1,

    @SerializedName("weathercode")
    val weatherCode: Int = 0
)
