package com.dantruong.smartvehicletelemetrydashboard_mobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.converter.AppConverter
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.dao.TemperatureDao
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.entity.Temperature

@Database(
    entities = [
        Temperature::class
    ],
    version = 1,
    exportSchema = false
)

@TypeConverters(AppConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun temperatureDao(): TemperatureDao
}