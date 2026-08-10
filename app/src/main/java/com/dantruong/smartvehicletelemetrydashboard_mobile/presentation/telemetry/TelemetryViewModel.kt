package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.telemetry

import androidx.lifecycle.ViewModel
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.repository.TelemetryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TelemetryViewModel @Inject constructor(
    private val telemetryRepository: TelemetryRepository
) : ViewModel() {

    val telemetryData = telemetryRepository.telemetryData
    private var serviceStarted = false

    fun startTelemetryService() {
        if (serviceStarted) return
        serviceStarted = true
        telemetryRepository.startAndBindService()
    }

    override fun onCleared() {
        super.onCleared()
        if (serviceStarted) {
            telemetryRepository.unbindService()
        }
    }
}
