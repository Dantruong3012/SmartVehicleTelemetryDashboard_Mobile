package com.dantruong.smartvehicletelemetrydashboard_mobile.domain.repository

import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.TelemetryData
import kotlinx.coroutines.flow.StateFlow

interface TelemetryRepository {
    val telemetryData: StateFlow<TelemetryData>
    val isBoundState: StateFlow<Boolean>
    fun startAndBindService()
    fun startBatteryCharging()
    fun unbindService()
    fun stopService()
}
