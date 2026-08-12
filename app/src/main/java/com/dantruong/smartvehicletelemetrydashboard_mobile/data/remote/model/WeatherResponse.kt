package com.dantruong.smartvehicletelemetrydashboard_mobile.data.remote.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("current_weather")
    val currentWeather: CurrentWeatherDto? = null,

    @SerializedName("hourly")
    val hourly: HourlyWeatherDto? = null,

    @SerializedName("daily")
    val daily: DailyWeatherDto? = null
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

data class HourlyWeatherDto(
    @SerializedName("time")
    val time: List<String> = emptyList(),

    @SerializedName("temperature_2m")
    val temperature: List<Double> = emptyList(),

    @SerializedName("weather_code")
    val weatherCode: List<Int> = emptyList(),

    @SerializedName("wind_speed_10m")
    val windSpeed: List<Double> = emptyList()
)

data class DailyWeatherDto(
    @SerializedName("time")
    val time: List<String> = emptyList(),

    @SerializedName("weather_code")
    val weatherCode: List<Int> = emptyList(),

    @SerializedName("temperature_2m_max")
    val maxTemperature: List<Double> = emptyList(),

    @SerializedName("temperature_2m_min")
    val minTemperature: List<Double> = emptyList()
)
