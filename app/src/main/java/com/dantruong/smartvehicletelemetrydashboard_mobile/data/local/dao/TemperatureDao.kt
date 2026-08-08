package com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface TemperatureDao {
    @Query("SELECT currentTemp FROM temperature ORDER BY timeChange DESC LIMIT 1")
    fun getCurrentTemp(): Int?
}
