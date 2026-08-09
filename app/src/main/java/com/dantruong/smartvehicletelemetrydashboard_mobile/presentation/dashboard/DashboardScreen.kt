package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

@Composable
fun DashboardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1014)) // Dark sleek background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header
        Spacer(modifier = Modifier.height(24.dp))
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

        Spacer(modifier = Modifier.height(48.dp))

        // HVAC Widget
        HvacScreen()

        // Future Widgets can be placed here (e.g. Speed, Media, GPS)
    }
}
