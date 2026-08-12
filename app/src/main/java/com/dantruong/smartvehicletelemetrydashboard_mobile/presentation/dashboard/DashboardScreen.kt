package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.door.DoorScreen
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.hvac.HvacScreen
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.telemetry.TelemetryScreen
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.weather.WeatherWidget
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.AccentBlue
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.DangerRed
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.PrimaryText

@Composable
fun DashboardScreen(
    onExitApp: () -> Unit = {},
    onOpenWeatherForecast: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1014))
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header
        Header(onExitApp = onExitApp)

        // Weather Widget
        WeatherWidget(onOpenForecast = onOpenWeatherForecast)

        // Telemetry Widget
        TelemetryScreen(
            modifier = Modifier.fillMaxWidth()
        )

        // HVAC Widget
        HvacScreen(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        )

        // Door Control Widget
        DoorScreen(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun Header(
    onExitApp: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(modifier = Modifier.size(42.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SMART VEHICLE",
                color = PrimaryText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )

            Text(
                text = "TELEMETRY DASHBOARD",
                color = AccentBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        IconButton(
            onClick = onExitApp,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                    color = DangerRed.copy(alpha = 0.2f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Rounded.PowerSettingsNew,
                contentDescription = "Power Off",
                tint = DangerRed
            )
        }
    }
}
