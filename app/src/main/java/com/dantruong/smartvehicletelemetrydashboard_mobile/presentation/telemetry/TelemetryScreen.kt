package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.telemetry

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TelemetryScreen(
    viewModel: TelemetryViewModel = hiltViewModel()
) {
    val telemetryData by viewModel.telemetryData.collectAsState()
    
    val animatedSpeed by animateFloatAsState(
        targetValue = telemetryData.speed.toFloat(),
        animationSpec = tween(300),
        label = "SpeedAnimation"
    )
    
    val animatedBattery by animateFloatAsState(
        targetValue = telemetryData.batteryLevel.toFloat(),
        animationSpec = tween(500),
        label = "BatteryAnimation"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Speedometer Widget
        TelemetryWidget(
            title = "SPEED",
            value = "${animatedSpeed.toInt()} km/h",
            icon = Icons.Rounded.Speed,
            color = Color(0xFF00E676)
        )

        // Battery Widget
        TelemetryWidget(
            title = "BATTERY",
            value = "${animatedBattery.toInt()}%",
            icon = Icons.Rounded.BatteryFull,
            color = if (animatedBattery > 20) Color(0xFF00B4D8) else Color(0xFFFF3D00)
        )
    }
}

@Composable
fun TelemetryWidget(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1E2026).copy(alpha = 0.5f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .padding(24.dp)
            .width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
        )
    }
}
