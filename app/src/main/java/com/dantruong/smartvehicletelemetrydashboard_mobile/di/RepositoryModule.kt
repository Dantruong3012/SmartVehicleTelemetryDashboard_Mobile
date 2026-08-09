package com.dantruong.smartvehicletelemetrydashboard_mobile.di

import com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.HvacRepository
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.HvacRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindHvacRepository(
        hvacRepositoryImpl: HvacRepositoryImpl
    ): HvacRepository
}