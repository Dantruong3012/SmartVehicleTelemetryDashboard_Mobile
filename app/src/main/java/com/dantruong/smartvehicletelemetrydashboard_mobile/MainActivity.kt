package com.dantruong.smartvehicletelemetrydashboard_mobile

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.dashboard.DashboardScreen
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.main.MainUiEvent
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.main.MainViewModel
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.weatherforecast.WeatherForecastScreen
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.DashboardBackground
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.SmartVehicleTelemetryDashboard_MobileTheme
import dagger.hilt.android.AndroidEntryPoint

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    private enum class AppScreen {
        Dashboard,
        WeatherForecast
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
        val dashboardBackgroundArgb = DashboardBackground.toArgb()
        window.setBackgroundDrawable(ColorDrawable(dashboardBackgroundArgb))
        window.statusBarColor = dashboardBackgroundArgb
        window.navigationBarColor = dashboardBackgroundArgb
        setContent {
            SmartVehicleTelemetryDashboard_MobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DashboardBackground
                ) {
                    var currentScreen by remember { mutableStateOf(AppScreen.Dashboard) }

                    LaunchedEffect(Unit) {
                        mainViewModel.events.collect { event ->
                            when (event) {
                                MainUiEvent.ExitApp -> {
                                    finishAndRemoveTask()
                                    finishAffinity()
                                }
                            }
                        }
                    }

                    when (currentScreen) {
                        AppScreen.Dashboard -> {
                            DashboardScreen(
                                onExitApp = mainViewModel::onPowerButtonClicked,
                                onOpenWeatherForecast = {
                                    currentScreen = AppScreen.WeatherForecast
                                }
                            )
                        }
                        AppScreen.WeatherForecast -> {
                            WeatherForecastScreen(
                                onBack = {
                                    currentScreen = AppScreen.Dashboard
                                }
                            )
                        }
                    }
                }
            }
        }
        window.decorView.post {
            window.decorView.invalidate()
        }
    }
}
