package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.hvac

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HvacScreen(
    viewModel: HvacViewModel = hiltViewModel()
) {
    val currentTemp by viewModel.currentTemp.collectAsState()
    val isHvacOn by viewModel.isHvacOn.collectAsState()

    // Premium Colors
    val activeColor = Color(0xFF00E676)
    val inactiveColor = Color(0xFFFF3D00)
    val glassColor = Color(0xFF1E2026).copy(alpha = 0.7f)
    
    val animatedBgColor by animateColorAsState(
        targetValue = if (isHvacOn) Color(0xFF0D1B2A) else Color(0xFF1B1B1B),
        animationSpec = tween(1000), label = ""
    )

    val powerButtonScale by animateFloatAsState(
        targetValue = if (isHvacOn) 1.05f else 1f,
        animationSpec = tween(500), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        animatedBgColor,
                        Color.Black
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "CLIMATE CONTROL",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Temperature Display
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .shadow(
                        elevation = if (isHvacOn) 30.dp else 10.dp,
                        shape = CircleShape,
                        spotColor = if (isHvacOn) Color(0xFF00B4D8) else Color.Transparent
                    )
                    .background(glassColor, CircleShape)
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                if (isHvacOn) Color(0xFF00B4D8) else Color.Gray,
                                if (isHvacOn) Color(0xFF90E0EF) else Color.DarkGray
                            )
                        ),
                        shape = CircleShape
                    )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$currentTemp°",
                        color = Color.White,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "SYNC",
                        color = if (isHvacOn) Color(0xFF00B4D8) else Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decrease Button
                TempControlButton(
                    icon = Icons.Default.Remove,
                    onClick = { viewModel.decreaseTemp() }
                )

                // Power Toggle Button
                Box(
                    modifier = Modifier
                        .scale(powerButtonScale)
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            if (isHvacOn) activeColor.copy(alpha = 0.2f) else inactiveColor.copy(alpha = 0.2f)
                        )
                        .clickable { viewModel.toggleHvac() }
                        .border(
                            2.dp,
                            if (isHvacOn) activeColor else inactiveColor,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PowerSettingsNew,
                        contentDescription = "Power",
                        tint = if (isHvacOn) activeColor else inactiveColor,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Increase Button
                TempControlButton(
                    icon = Icons.Default.Add,
                    onClick = { viewModel.increaseTemp() }
                )
            }
        }
    }
}

@Composable
fun TempControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.05f))
            .clickable { onClick() }
            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Control",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}