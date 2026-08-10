package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.door

import androidx.lifecycle.ViewModel
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.DoorStateListener
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.repository.DoorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DoorViewModel @Inject constructor(
    private val doorRepository: DoorRepository
) : ViewModel(), DoorStateListener {

    private val _doorStates = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val doorStates: StateFlow<Map<Int, Boolean>> = _doorStates.asStateFlow()

    private val _doorPendingStates = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val doorPendingStates: StateFlow<Map<Int, Boolean>> = _doorPendingStates.asStateFlow()

    private val _doorEvent = MutableStateFlow<DoorEvent?>(null)
    val doorEvent: StateFlow<DoorEvent?> = _doorEvent.asStateFlow()

    init {
        doorRepository.registerListener(this)

        val initialMap = mapOf(
            1 to doorRepository.isDoorOpen(1),
            2 to doorRepository.isDoorOpen(2),
            3 to doorRepository.isDoorOpen(3),
            4 to doorRepository.isDoorOpen(4)
        )
        _doorStates.value = initialMap
    }

    override fun onDoorStateChanged(doorId: Int, isOpen: Boolean) {
        _doorStates.update { currentMap ->
            val newMap = currentMap.toMutableMap()
            newMap[doorId] = isOpen
            newMap
        }
        
        _doorPendingStates.update { currentMap ->
            val newMap = currentMap.toMutableMap()
            newMap.remove(doorId)
            newMap
        }

        val eventMsg = if (isOpen) "Door $doorId is now OPEN" else "Door $doorId is now CLOSED"
        _doorEvent.value = DoorEvent(eventMsg)
    }

    fun toggleDoor(doorId: Int) {
        val currentState = _doorStates.value[doorId] ?: false
        
        _doorPendingStates.update { currentMap ->
            val newMap = currentMap.toMutableMap()
            newMap[doorId] = true
            newMap
        }

        if (currentState) {
            doorRepository.requestCloseDoor(doorId)
        } else {
            doorRepository.requestOpenDoor(doorId)
        }
    }

    fun clearEvent() {
        _doorEvent.value = null
    }

    override fun onCleared() {
        super.onCleared()
        doorRepository.unregisterListener(this)
    }
}

data class DoorEvent(val message: String)
