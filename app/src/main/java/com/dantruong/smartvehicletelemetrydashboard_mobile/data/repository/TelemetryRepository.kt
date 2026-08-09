package com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository

import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.TelemetryData
import kotlinx.coroutines.flow.StateFlow

interface TelemetryRepository {
    val telemetryData: StateFlow<TelemetryData>
    fun startAndBindService()
    fun unbindService()
    fun stopService()
}
