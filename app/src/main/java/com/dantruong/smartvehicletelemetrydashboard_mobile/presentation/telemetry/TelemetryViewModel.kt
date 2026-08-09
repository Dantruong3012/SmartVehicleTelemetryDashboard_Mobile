package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.telemetry

import androidx.lifecycle.ViewModel
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.TelemetryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TelemetryViewModel @Inject constructor(
    private val telemetryRepository: TelemetryRepository
) : ViewModel() {

    val telemetryData = telemetryRepository.telemetryData

    init {
        telemetryRepository.startAndBindService()
    }

    override fun onCleared() {
        super.onCleared()
        telemetryRepository.unbindService()
    }
}
