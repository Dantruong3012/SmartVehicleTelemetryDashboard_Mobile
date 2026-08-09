package com.dantruong.smartvehicletelemetrydashboard_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.dantruong.smartvehicletelemetrydashboard_mobile.framework.services.TelemetryService
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.dashboard.DashboardScreen
import com.dantruong.smartvehicletelemetrydashboard_mobile.ui.theme.SmartVehicleTelemetryDashboard_MobileTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartVehicleTelemetryDashboard_MobileTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DashboardScreen(
                        onExitApp = {
                            // TelemetryService tự dọn dẹp HvacEngineService trong onDestroy()
                            stopService(android.content.Intent(this@MainActivity, TelemetryService::class.java))
                            finishAndRemoveTask()
                        }
                    )
                }
            }
        }
    }
}