package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.hvac

import androidx.lifecycle.ViewModel
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.HvacRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HvacViewModel @Inject constructor(
    private val hvacRepository: HvacRepository
) : ViewModel() {

    val currentTemp: StateFlow<Int> = hvacRepository.currentTemp
    val isHvacOn: StateFlow<Boolean> = hvacRepository.isHvacOn

    init {
        hvacRepository.startAndBindService()
    }

    fun toggleHvac() {
        if (isHvacOn.value) {
            hvacRepository.turnOffHvac()
        } else {
            hvacRepository.turnOnHvac()
        }
    }

    fun increaseTemp() {
        if (!isHvacOn.value) return
        val newTemp = (currentTemp.value + 1).coerceAtMost(com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.HvacConfig.MAX_TEMPERATURE)
        hvacRepository.setTemperature(newTemp)
    }

    fun decreaseTemp() {
        if (!isHvacOn.value) return
        val newTemp = (currentTemp.value - 1).coerceAtLeast(com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.HvacConfig.MIN_TEMPERATURE)
        hvacRepository.setTemperature(newTemp)
    }

    override fun onCleared() {
        super.onCleared()
        hvacRepository.unbindService()
    }
}
