package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.telemetry

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.DeviceThermostat
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
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
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.AccentBlue
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.DangerRed
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.PanelBackground
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.PrimaryText
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.SecondaryText
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.SuccessGreen
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.WarningYellow
import kotlinx.coroutines.delay

@Composable
fun TelemetryScreen(
    modifier: Modifier = Modifier,
    viewModel: TelemetryViewModel = hiltViewModel()
) {
    val telemetryData by viewModel.telemetryData.collectAsState()
    var batteryDepletedHandled by remember { mutableStateOf(false) }

    LaunchedEffect(telemetryData.batteryLevel) {
        if (telemetryData.batteryLevel > 0) {
            batteryDepletedHandled = false
        }
    }

    if (telemetryData.batteryLevel == 0 && !batteryDepletedHandled) {
        AlertDialog(
            onDismissRequest = {
                batteryDepletedHandled = true
            },
            title = {
                Text(text = "Battery Depleted")
            },
            text = {
                Text(text = "Would you like to charge the battery to continue operating?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        batteryDepletedHandled = true
                        viewModel.startBatteryCharging()
                    }
                ) {
                    Text(text = "Charge Battery")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        batteryDepletedHandled = true
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    val alertMessage = when {
        telemetryData.batteryLevel == 0 -> "Warning: Battery depleted, engine stopped"
        telemetryData.batteryLevel <= 10 -> "Warning: Low battery level"
        telemetryData.engineTemperature > 100 -> "Warning: High engine temperature"
        else -> "No active alerts"
    }
    val alertColor = if (telemetryData.batteryLevel <= 10 || telemetryData.engineTemperature > 100) {
        DangerRed
    } else {
        SuccessGreen
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(PanelBackground.copy(alpha = 0.45f))
                .border(1.dp, AccentBlue.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Sensors,
                contentDescription = "Telemetry status",
                tint = AccentBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Vehicle Connected",
                color = PrimaryText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Telemetry Service Running",
                color = SecondaryText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(118.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TelemetryWidget(
                title = "SPEED",
                value = "${telemetryData.speed} km/h",
                icon = Icons.Rounded.Speed,
                color = SuccessGreen,
                modifier = Modifier.weight(1f)
            )

            TelemetryWidget(
                title = "BATTERY",
                value = "${telemetryData.batteryLevel}%",
                icon = Icons.Rounded.BatteryFull,
                color = if (telemetryData.batteryLevel > 20) AccentBlue else DangerRed,
                modifier = Modifier.weight(1f)
            )

            TelemetryWidget(
                title = "ENGINE TEMP",
                value = "${telemetryData.engineTemperature}°C",
                icon = Icons.Rounded.DeviceThermostat,
                color = if (telemetryData.engineTemperature > 100) DangerRed else WarningYellow,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(alertColor.copy(alpha = 0.12f))
                .border(1.dp, alertColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.WarningAmber,
                contentDescription = "Latest alert",
                tint = alertColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = alertMessage,
                color = PrimaryText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun TelemetryWidget(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(18.dp))
            .background(PanelBackground.copy(alpha = 0.5f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            color = PrimaryText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            color = SecondaryText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
        )
    }
}
