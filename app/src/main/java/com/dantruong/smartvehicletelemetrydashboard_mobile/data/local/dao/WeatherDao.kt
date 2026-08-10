package com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.entity.WeatherEntity

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("SELECT * FROM weather WHERE id = 1 LIMIT 1")
    suspend fun getCachedWeather(): WeatherEntity?
}