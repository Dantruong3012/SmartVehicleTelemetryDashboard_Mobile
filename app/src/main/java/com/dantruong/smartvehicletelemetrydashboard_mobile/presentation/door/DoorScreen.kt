package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.door

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MeetingRoom
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DoorScreen(
    viewModel: DoorViewModel = hiltViewModel()
) {
    val doorStates by viewModel.doorStates.collectAsState()
    val doorPendingStates by viewModel.doorPendingStates.collectAsState()
    val doorEvent by viewModel.doorEvent.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(doorEvent) {
        doorEvent?.let { event ->
            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            viewModel.clearEvent()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1A1C23))
            .padding(20.dp)
    ) {
        Text(
            text = "DOOR CONTROL",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Door 1 and 2
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DoorButton(
                    doorId = 1,
                    isOpen = doorStates[1] == true,
                    isPending = doorPendingStates[1] == true,
                    onClick = { viewModel.toggleDoor(1) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                DoorButton(
                    doorId = 3,
                    isOpen = doorStates[3] == true,
                    isPending = doorPendingStates[3] == true,
                    onClick = { viewModel.toggleDoor(3) }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Door 3 and 4
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DoorButton(
                    doorId = 2,
                    isOpen = doorStates[2] == true,
                    isPending = doorPendingStates[2] == true,
                    onClick = { viewModel.toggleDoor(2) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                DoorButton(
                    doorId = 4,
                    isOpen = doorStates[4] == true,
                    isPending = doorPendingStates[4] == true,
                    onClick = { viewModel.toggleDoor(4) }
                )
            }
        }
    }
}

@Composable
fun DoorButton(
    doorId: Int,
    isOpen: Boolean,
    isPending: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isOpen) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFF2C2F3A)
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (isOpen) Color(0xFF00E676) else Color(0xFF90A4AE)
    )

    Box(
        modifier = Modifier
            .size(100.dp, 100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(enabled = !isPending) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isPending) {
            CircularProgressIndicator(
                color = Color(0xFF00B4D8),
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.MeetingRoom,
                    contentDescription = "Door $doorId",
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "DOOR $doorId",
                    color = iconColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isOpen) "OPEN" else "CLOSED",
                    color = iconColor.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }
        }
    }
}
