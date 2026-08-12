package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.usecase.ShutdownAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val shutdownAppUseCase: ShutdownAppUseCase
) : ViewModel() {
    private val _events = MutableSharedFlow<MainUiEvent>()
    val events: SharedFlow<MainUiEvent> = _events.asSharedFlow()

    fun onPowerButtonClicked() {
        viewModelScope.launch {
            shutdownAppUseCase()
            _events.emit(MainUiEvent.ExitApp)
        }
    }
}
