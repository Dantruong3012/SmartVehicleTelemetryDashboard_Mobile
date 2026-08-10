package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.hvac

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.AccentBlue
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.DangerRed
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.HvacActiveBackground
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.HvacInactiveBackground
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.PanelBackground
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.PrimaryText
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.SecondaryText
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.SuccessGreen
import kotlinx.coroutines.delay

@Composable
fun HvacScreen(
    modifier: Modifier = Modifier,
    viewModel: HvacViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        delay(500)
        viewModel.startHvacService()
    }

    val currentTemp by viewModel.currentTemp.collectAsState()
    val isHvacOn by viewModel.isHvacOn.collectAsState()

    val activeColor = SuccessGreen
    val inactiveColor = DangerRed
    val glassColor = PanelBackground.copy(alpha = 0.7f)
    
    val powerButtonScale by animateFloatAsState(
        targetValue = if (isHvacOn) 1.05f else 1f,
        animationSpec = tween(500), label = ""
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(if (isHvacOn) HvacActiveBackground else HvacInactiveBackground)
            .border(1.dp, PrimaryText.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CLIMATE CONTROL",
                    color = PrimaryText.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isHvacOn) "AC ECU Connected" else "Đang chờ lệnh ECU",
                    color = if (isHvacOn) SuccessGreen else SecondaryText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                TemperatureDial(
                    currentTemp = currentTemp,
                    isHvacOn = isHvacOn,
                    glassColor = glassColor,
                    modifier = Modifier
                        .fillMaxHeight(0.86f)
                        .aspectRatio(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TempControlButton(
                    icon = Icons.Default.Remove,
                    onClick = { viewModel.decreaseTemp() }
                )

                IconButton(
                    onClick = { viewModel.toggleHvac() },
                    modifier = Modifier
                        .scale(powerButtonScale)
                        .size(66.dp)
                        .clip(CircleShape)
                        .background(
                            if (isHvacOn) activeColor.copy(alpha = 0.2f) else inactiveColor.copy(alpha = 0.2f)
                        )
                        .border(
                            2.dp,
                            if (isHvacOn) activeColor else inactiveColor,
                            CircleShape
                        )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.PowerSettingsNew,
                            contentDescription = "Power",
                            tint = if (isHvacOn) activeColor else inactiveColor,
                            modifier = Modifier.size(27.dp)
                        )
                        Text(
                            text = if (isHvacOn) "ON" else "OFF",
                            color = if (isHvacOn) activeColor else inactiveColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                TempControlButton(
                    icon = Icons.Default.Add,
                    onClick = { viewModel.increaseTemp() }
                )
            }
        }
    }
}

@Composable
private fun TemperatureDial(
    currentTemp: Int,
    isHvacOn: Boolean,
    glassColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(glassColor)
            .border(
                width = 2.dp,
                color = if (isHvacOn) AccentBlue else SecondaryText,
                shape = CircleShape
            )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$currentTemp°",
                color = PrimaryText,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = if (isHvacOn) "SYNC" else "OFF",
                color = if (isHvacOn) AccentBlue else SecondaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun TempControlButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(PrimaryText.copy(alpha = 0.05f))
            .clickable { onClick() }
            .border(1.dp, PrimaryText.copy(alpha = 0.1f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Control",
            tint = PrimaryText,
            modifier = Modifier.size(24.dp)
        )
    }
}
