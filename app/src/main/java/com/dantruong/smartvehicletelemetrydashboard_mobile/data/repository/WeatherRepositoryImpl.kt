package com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository

import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.dao.WeatherDao
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.entity.WeatherEntity
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.remote.WeatherApiService
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.WeatherData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApiService: WeatherApiService,
    private val  weatherDao: WeatherDao,
) : WeatherRepository {

    override suspend fun getWeatherData(): Result<WeatherData> {
        return try {
            val response = weatherApiService.getCurrentWeather()
            val dto = response.currentWeather
            if (dto != null) {
                val data = WeatherData(
                    temperature = dto.temperature,
                    windSpeed = dto.windspeed,
                    isDay = dto.isDay == 1,
                    weatherCode = dto.weatherCode
                )
                weatherDao.insertWeather(WeatherEntity(
                    id = 1,
                    temperature = data.temperature,
                    windSpeed = data.windSpeed,
                    isDay = data.isDay,
                    weatherCode = data.weatherCode
                ))
                Result.success(data)
            } else {
                Result.failure(Exception("Weather data is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCachedWeatherData(): WeatherData? {
        val entity = weatherDao.getCachedWeather()
        return if (entity != null) {
            WeatherData(
                temperature = entity.temperature,
                windSpeed = entity.windSpeed,
                isDay = entity.isDay,
                weatherCode = entity.weatherCode
            )
        } else {
            null
        }
    }
}
