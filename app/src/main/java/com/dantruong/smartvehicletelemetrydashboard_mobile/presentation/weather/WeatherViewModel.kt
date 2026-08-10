package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.WeatherRepository
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.WeatherData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

sealed interface WeatherUiState {
    object Loading : WeatherUiState
    data class Success(val weatherData: WeatherData) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    init {
        loadInitialWeather()
    }

    private fun loadInitialWeather() {
        viewModelScope.launch {
            val cached = weatherRepository.getCachedWeatherData()
            if (cached != null) {
                _uiState.value = WeatherUiState.Success(cached)
            } else {
                fetchWeather(isManualTap = false)
            }
        }
    }

    fun fetchWeather(isManualTap: Boolean = true) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            
            weatherRepository.getWeatherData()
                .onSuccess { data ->
                    _uiState.value = WeatherUiState.Success(data)
                }
                .onFailure { error ->
                    if (error is UnknownHostException || error is IOException) {
                        if (isManualTap) {
                            _toastEvent.emit("No internet connection. Displaying cached data.")
                        }
                        
                        val cached = weatherRepository.getCachedWeatherData()
                        if (cached != null) {
                            _uiState.value = WeatherUiState.Success(cached)
                        } else {
                            _uiState.value = WeatherUiState.Error("No network and no cached data available.")
                        }
                    } else {
                        _uiState.value = WeatherUiState.Error(error.message ?: "Failed to load weather")
                    }
                }
        }
    }
}
