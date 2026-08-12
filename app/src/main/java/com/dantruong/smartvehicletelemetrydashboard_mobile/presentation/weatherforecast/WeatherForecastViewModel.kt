package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.weatherforecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.WeatherRepository
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.WeatherForecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface WeatherForecastUiState {
    object Loading : WeatherForecastUiState
    data class Success(val forecast: WeatherForecast) : WeatherForecastUiState
    data class Error(val message: String) : WeatherForecastUiState
}

@HiltViewModel
class WeatherForecastViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherForecastUiState>(WeatherForecastUiState.Loading)
    val uiState: StateFlow<WeatherForecastUiState> = _uiState.asStateFlow()

    init {
        fetchForecast()
    }

    fun fetchForecast() {
        viewModelScope.launch {
            _uiState.value = WeatherForecastUiState.Loading
            weatherRepository.getWeatherForecast()
                .onSuccess { forecast ->
                    _uiState.value = WeatherForecastUiState.Success(forecast)
                }
                .onFailure { error ->
                    _uiState.value = WeatherForecastUiState.Error(
                        error.message ?: "Failed to load forecast"
                    )
                }
        }
    }
}
