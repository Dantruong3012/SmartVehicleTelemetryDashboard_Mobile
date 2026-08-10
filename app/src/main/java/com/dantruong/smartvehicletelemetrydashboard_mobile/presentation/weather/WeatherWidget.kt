package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.weather

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.WeatherData

@Composable
fun WeatherWidget(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1E2026).copy(alpha = 0.5f))
            .border(1.dp, Color(0xFF00B4D8).copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .clickable { viewModel.fetchWeather(isManualTap = true) }
            .padding(20.dp)
    ) {
        Crossfade(targetState = uiState, label = "WeatherStateAnimation") { state ->
            when (state) {
                is WeatherUiState.Loading -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF00B4D8),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Fetching Weather...", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                is WeatherUiState.Success -> {
                    WeatherContent(data = state.weatherData)
                }
                is WeatherUiState.Error -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CloudOff,
                            contentDescription = "Error",
                            tint = Color(0xFFFF3D00)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tap to retry weather",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherContent(data: WeatherData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = getWeatherIcon(data.weatherCode, data.isDay),
                contentDescription = "Weather Icon",
                tint = if (data.isDay) Color(0xFFFFD54F) else Color(0xFF90CAF9),
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "${data.temperature.toInt()}°C",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = getWeatherDescription(data.weatherCode),
                    color = Color(0xFF00B4D8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Air,
                    contentDescription = "Wind",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${data.windSpeed} km/h",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = "Location",
                    tint = Color(0xFFFF3D00),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "Hà Nội",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

private fun getWeatherIcon(code: Int, isDay: Boolean): ImageVector {
    return when (code) {
        0 -> if (isDay) Icons.Rounded.WbSunny else Icons.Rounded.NightsStay
        1, 2, 3 -> Icons.Rounded.WbCloudy
        45, 48 -> Icons.Rounded.Cloud
        51, 53, 55, 61, 63, 65 -> Icons.Rounded.Thunderstorm
        80, 81, 82 -> Icons.Rounded.Grain
        else -> Icons.Rounded.WbSunny
    }
}

private fun getWeatherDescription(code: Int): String {
    return when (code) {
        0 -> "Clear Sky"
        1, 2, 3 -> "Partly Cloudy"
        45, 48 -> "Foggy"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rainy"
        80, 81, 82 -> "Showers"
        else -> "Sunny"
    }
}
