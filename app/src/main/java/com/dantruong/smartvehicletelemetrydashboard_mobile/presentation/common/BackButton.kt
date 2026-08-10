package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.AccentBlue

@Composable
fun BackButton(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "Back"
) {
    IconButton(
        onClick = onBack,
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(
                color = AccentBlue.copy(alpha = 0.18f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = contentDescription,
            tint = Color.White
        )
    }
}
