package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.repository.HvacRepository
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.repository.TelemetryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val hvacRepository: HvacRepository,
    private val telemetryRepository: TelemetryRepository
) : ViewModel() {

    val isSystemReady: StateFlow<Boolean> = combine(
        hvacRepository.isBoundState,
        telemetryRepository.isBoundState
    ) { hvacBound, telemetryBound ->
        hvacBound && telemetryBound
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun startServices() {
        hvacRepository.startAndBindService()
        telemetryRepository.startAndBindService()
    }
}
