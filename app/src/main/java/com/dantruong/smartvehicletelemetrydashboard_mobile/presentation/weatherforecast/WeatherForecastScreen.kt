package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.weatherforecast

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Thunderstorm
import androidx.compose.material.icons.rounded.WbCloudy
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.DailyWeatherForecast
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.HourlyWeatherForecast
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.WeatherForecast
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.common.BackButton
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.AccentBlue
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.DashboardBackground
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.PanelBackground
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.PrimaryText
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.SecondaryText
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WeatherForecastScreen(
    onBack: () -> Unit,
    viewModel: WeatherForecastViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        WeatherForecastHeader(
            onBack = onBack,
            onRefresh = viewModel::fetchForecast
        )

        when (val state = uiState) {
            is WeatherForecastUiState.Loading -> LoadingForecast()
            is WeatherForecastUiState.Success -> ForecastContent(forecast = state.forecast)
            is WeatherForecastUiState.Error -> ErrorForecast(
                message = state.message,
                onRetry = viewModel::fetchForecast
            )
        }
    }
}

@Composable
private fun WeatherForecastHeader(
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton(onBack = onBack, contentDescription = "Back to dashboard")

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "WEATHER FORECAST",
                color = PrimaryText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Text(
                text = "Hourly and 7-day outlook",
                color = AccentBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        IconButton(
            onClick = onRefresh,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(21.dp))
                .background(PanelBackground)
        ) {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = "Refresh forecast",
                tint = AccentBlue
            )
        }
    }
}

@Composable
private fun ForecastContent(forecast: WeatherForecast) {
    var selectedDate by remember(forecast.daily) {
        mutableStateOf(forecast.daily.firstOrNull()?.date.orEmpty())
    }
    val selectedHourlyForecast = forecast.hourly.filter { hour ->
        hour.time.startsWith("${selectedDate}T")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(forecast.daily) { day ->
                DailyForecastCard(
                    day = day,
                    isSelected = day.date == selectedDate,
                    onClick = { selectedDate = day.date }
                )
            }
        }

        HourlyForecastRecyclerView(
            items = selectedHourlyForecast,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
private fun DailyForecastCard(
    day: DailyWeatherForecast,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(118.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) AccentBlue.copy(alpha = 0.2f) else PanelBackground.copy(alpha = 0.72f))
            .border(
                width = 1.dp,
                color = if (isSelected) AccentBlue else AccentBlue.copy(alpha = 0.2f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = formatDay(day.date),
            color = SecondaryText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Icon(
            imageVector = weatherIcon(day.weatherCode, isDay = true),
            contentDescription = "Daily weather",
            tint = weatherTint(day.weatherCode, isDay = true),
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = "${day.maxTemperature.toInt()}° / ${day.minTemperature.toInt()}°",
            color = PrimaryText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = weatherDescription(day.weatherCode),
            color = AccentBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun HourlyForecastRecyclerView(
    items: List<HourlyWeatherForecast>,
    modifier: Modifier = Modifier
) {
    val adapter = remember { HourlyWeatherAdapter() }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            RecyclerView(context).apply {
                val topPadding = (4 * context.resources.displayMetrics.density).toInt()
                layoutManager = LinearLayoutManager(context)
                this.adapter = adapter
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                setPadding(0, topPadding, 0, 0)
                clipToPadding = true
            }
        },
        update = {
            adapter.submitList(items)
        }
    )
}

@Composable
private fun LoadingForecast() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(
                color = AccentBlue,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Loading forecast...",
                color = SecondaryText,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ErrorForecast(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(PanelBackground.copy(alpha = 0.72f))
            .border(1.dp, Color(0xFFFF3D00).copy(alpha = 0.25f), RoundedCornerShape(22.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.CloudOff,
            contentDescription = "Forecast error",
            tint = Color(0xFFFF3D00),
            modifier = Modifier.size(34.dp)
        )
        Text(
            text = message,
            color = SecondaryText,
            fontSize = 14.sp
        )
        IconButton(
            onClick = onRetry,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(21.dp))
                .background(AccentBlue.copy(alpha = 0.18f))
        ) {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = "Retry forecast",
                tint = AccentBlue
            )
        }
    }
}

private fun formatDay(date: String): String {
    return runCatching {
        LocalDate.parse(date).format(DateTimeFormatter.ofPattern("EEE, dd/MM"))
    }.getOrDefault(date)
}

private fun weatherIcon(code: Int, isDay: Boolean): ImageVector {
    return when (code) {
        0 -> if (isDay) Icons.Rounded.WbSunny else Icons.Rounded.NightsStay
        1, 2, 3 -> Icons.Rounded.WbCloudy
        45, 48 -> Icons.Rounded.Cloud
        51, 53, 55, 61, 63, 65 -> Icons.Rounded.Thunderstorm
        80, 81, 82 -> Icons.Rounded.Grain
        else -> Icons.Rounded.WbSunny
    }
}

private fun weatherTint(code: Int, isDay: Boolean): Color {
    return when (code) {
        0 -> if (isDay) Color(0xFFFFD54F) else Color(0xFF90CAF9)
        61, 63, 65, 80, 81, 82 -> Color(0xFF64B5F6)
        51, 53, 55 -> Color(0xFF80DEEA)
        else -> AccentBlue
    }
}

private fun weatherDescription(code: Int): String {
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
