package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.hvac.HvacScreen
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.telemetry.TelemetryScreen
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.weather.WeatherWidget

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun DashboardScreen(onExitApp: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1014)) // Dark sleek background
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SMART VEHICLE",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "TELEMETRY DASHBOARD",
                    color = Color(0xFF00B4D8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
            androidx.compose.material3.IconButton(
                onClick = onExitApp,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFF3D00).copy(alpha = 0.2f), androidx.compose.foundation.shape.CircleShape)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Rounded.PowerSettingsNew,
                    contentDescription = "Power Off",
                    tint = Color(0xFFFF3D00)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Weather Widget
        WeatherWidget()

        Spacer(modifier = Modifier.height(16.dp))

        // Telemetry Widget
        TelemetryScreen()

        Spacer(modifier = Modifier.height(16.dp))

        // HVAC Widget
        HvacScreen()

        Spacer(modifier = Modifier.height(16.dp))

        // Door Control Widget
        com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.door.DoorScreen()
    }
}
