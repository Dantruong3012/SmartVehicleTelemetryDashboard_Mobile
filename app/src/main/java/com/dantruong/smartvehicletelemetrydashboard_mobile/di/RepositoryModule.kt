package com.dantruong.smartvehicletelemetrydashboard_mobile.di

import com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.AppShutdownRepositoryImpl
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.DoorRepositoryImpl
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.HvacRepositoryImpl
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.TelemetryRepositoryImpl
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.repository.AppShutdownRepository
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.repository.DoorRepository
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.repository.HvacRepository
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.repository.TelemetryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindHvacRepository(
        hvacRepositoryImpl: HvacRepositoryImpl
    ): HvacRepository

    @Binds
    @Singleton
    abstract fun bindTelemetryRepository(
        telemetryRepositoryImpl: TelemetryRepositoryImpl
    ): TelemetryRepository

    @Binds
    @Singleton
    abstract fun bindAppShutdownRepository(
        appShutdownRepositoryImpl: AppShutdownRepositoryImpl
    ): AppShutdownRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.WeatherRepositoryImpl
    ): com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.WeatherRepository

    @Binds
    @Singleton
    abstract fun bindDoorRepository(
        doorRepositoryImpl: DoorRepositoryImpl
    ): DoorRepository
}
