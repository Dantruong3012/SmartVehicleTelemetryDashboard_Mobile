package com.dantruong.smartvehicletelemetrydashboard_mobile

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.DoorRepository
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.repository.HvacRepository
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.repository.TelemetryRepository
import com.dantruong.smartvehicletelemetrydashboard_mobile.framework.services.DoorControlService
import com.dantruong.smartvehicletelemetrydashboard_mobile.framework.services.HvacEngineService
import com.dantruong.smartvehicletelemetrydashboard_mobile.framework.services.TelemetryService
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.dashboard.DashboardScreen
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.weatherforecast.WeatherForecastScreen
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.DashboardBackground
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.SmartVehicleTelemetryDashboard_MobileTheme
import dagger.hilt.android.AndroidEntryPoint

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var telemetryRepository: TelemetryRepository
    @Inject lateinit var hvacRepository: HvacRepository
    @Inject lateinit var doorRepository: DoorRepository

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

                    when (currentScreen) {
                        AppScreen.Dashboard -> {
                            DashboardScreen(
                                onExitApp = {
                                    // Unbind all repositories first
                                    telemetryRepository.unbindService()
                                    hvacRepository.unbindService()
                                    doorRepository.unbindService()

                                    stopService(android.content.Intent(this@MainActivity, TelemetryService::class.java))
                                    stopService(android.content.Intent(this@MainActivity, HvacEngineService::class.java))
                                    stopService(android.content.Intent(this@MainActivity, DoorControlService::class.java))
                                    finishAndRemoveTask()
                                },
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
