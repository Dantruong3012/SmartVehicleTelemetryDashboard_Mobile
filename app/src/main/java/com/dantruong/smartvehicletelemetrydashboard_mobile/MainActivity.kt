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

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
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