package com.dantruong.smartvehicletelemetrydashboard_mobile.di

import android.content.Context
import androidx.room.Room
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.AppDatabase
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.dao.AlertLogDao
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.dao.TemperatureDao
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.dao.WeatherDao
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.dao.TripLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "smart-vehicle"
        )
            .enableMultiInstanceInvalidation()
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTemperatureDao(database: AppDatabase): TemperatureDao{
        return database.temperatureDao()
    }

    @Provides
    fun provideWeatherDao(database: AppDatabase): com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.dao.WeatherDao {
        return database.weatherDao()
    }
}
    fun provideTripLogDao(database: AppDatabase): TripLogDao {
        return database.tripLogDao()
    }

    @Provides
    fun provideAlertLogDao(database: AppDatabase): AlertLogDao {
        return database.alertLogDao()
    }
}
