package com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository

import kotlinx.coroutines.flow.StateFlow

interface HvacRepository {
    val currentTemp: StateFlow<Int>
    val isHvacOn: StateFlow<Boolean>
    fun turnOnHvac()
    fun turnOffHvac()
    fun setTemperature(temp: Int)
    fun startAndBindService()
    fun unbindService()
    fun stopService()
}
