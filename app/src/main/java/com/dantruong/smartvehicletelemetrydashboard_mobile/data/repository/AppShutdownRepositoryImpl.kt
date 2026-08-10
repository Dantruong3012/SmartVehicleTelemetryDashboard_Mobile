package com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository

import android.content.Context
import android.content.Intent
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.repository.AppShutdownRepository
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.repository.DoorRepository
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.repository.HvacRepository
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.repository.TelemetryRepository
import com.dantruong.smartvehicletelemetrydashboard_mobile.framework.services.DoorControlService
import com.dantruong.smartvehicletelemetrydashboard_mobile.framework.services.HvacEngineService
import com.dantruong.smartvehicletelemetrydashboard_mobile.framework.services.TelemetryService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppShutdownRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val telemetryRepository: TelemetryRepository,
    private val hvacRepository: HvacRepository,
    private val doorRepository: DoorRepository
) : AppShutdownRepository {
    override fun shutdownServices() {
        telemetryRepository.unbindService()
        hvacRepository.unbindService()
        doorRepository.shutdown()

        context.stopService(Intent(context, DoorControlService::class.java))
        context.stopService(Intent(context, HvacEngineService::class.java))
        telemetryRepository.stopService()
        context.stopService(Intent(context, TelemetryService::class.java))
    }
}
