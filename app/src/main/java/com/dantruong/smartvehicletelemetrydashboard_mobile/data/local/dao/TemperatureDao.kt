package com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.entity.Temperature

@Dao
interface TemperatureDao {
    @Query("SELECT currentTemp FROM temperature ORDER BY timeChange DESC LIMIT 1")
    suspend fun getCurrentTemp(): Int?

    @Insert
    suspend fun insertTemp(temperature: Temperature)
}
