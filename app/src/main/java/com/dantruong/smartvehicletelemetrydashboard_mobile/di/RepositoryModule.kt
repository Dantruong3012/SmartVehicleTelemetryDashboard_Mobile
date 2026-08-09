package com.dantruong.smartvehicletelemetrydashboard_mobile.di

import com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.HvacRepository
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.HvacRepositoryImpl
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.TelemetryRepository
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.TelemetryRepositoryImpl
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
}