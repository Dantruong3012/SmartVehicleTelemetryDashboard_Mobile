package com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.TelemetryListener
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.TelemetryData
import com.dantruong.smartvehicletelemetrydashboard_mobile.framework.services.TelemetryService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelemetryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TelemetryRepository {
    private var telemetryService: TelemetryService? = null
    private val _telemetryData = MutableStateFlow(TelemetryData(0, 100))
    override val telemetryData: StateFlow<TelemetryData> = _telemetryData.asStateFlow()
    private val listener = TelemetryListener { data ->
        _telemetryData.value = data
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TelemetryService.LocalBinder
            telemetryService = binder.service
            telemetryService?.setTelemetryListener(listener)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            telemetryService?.setTelemetryListener(null)
            telemetryService = null
        }
    }

    override fun startAndBindService() {
        val intent = Intent(context, TelemetryService::class.java)
        context.startForegroundService(intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun unbindService() {
        telemetryService?.setTelemetryListener(null)
        context.unbindService(connection)
        telemetryService = null
    }

    override fun stopService() {
        val intent = Intent(context, TelemetryService::class.java)
        context.stopService(intent)
    }
}