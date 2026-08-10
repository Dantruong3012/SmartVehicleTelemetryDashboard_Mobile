package com.dantruong.smartvehicletelemetrydashboard_mobile.domain.repository

import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.DoorStateListener

interface DoorRepository {
    fun requestOpenDoor(doorId: Int)
    fun requestCloseDoor(doorId: Int)
    fun isDoorOpen(doorId: Int): Boolean
    fun registerListener(listener: DoorStateListener)
    fun unregisterListener(listener: DoorStateListener)
    fun shutdown()
}
