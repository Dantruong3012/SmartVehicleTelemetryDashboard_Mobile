package com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.dao.TemperatureDao
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.entity.Temperature
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.HvacConfig
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.HvacStateListener
import com.dantruong.smartvehicletelemetrydashboard_mobile.framework.services.TelemetryService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Flow:
 *   ViewModel -> HvacRepositoryImpl -> TelemetryService -> (AIDL) -> HvacEngineService
 *   HvacEngineService -> (Callback) -> TelemetryService -> HvacStateListener -> HvacRepositoryImpl -> StateFlow -> UI
 */
@Singleton
class HvacRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val temperatureDao: TemperatureDao
) : HvacRepository {

    private var telemetryService: TelemetryService? = null

    private val _currentTemp = MutableStateFlow(HvacConfig.DEFAULT_TEMPERATURE)
    override val currentTemp: StateFlow<Int> = _currentTemp.asStateFlow()

    private val _isHvacOn = MutableStateFlow(false)
    override val isHvacOn: StateFlow<Boolean> = _isHvacOn.asStateFlow()

    private val hvacStateListener = object : HvacStateListener {
        override fun onTemperatureChanged(temp: Int) {
            if (_currentTemp.value != temp) {
                _currentTemp.value = temp
                CoroutineScope(Dispatchers.IO).launch {
                    temperatureDao.insertTemp(Temperature(currentTemp = temp))
                }
            }
        }

        override fun onHvacStateChanged(isOn: Boolean) {
            _isHvacOn.value = isOn
            if (!isOn) {
                CoroutineScope(Dispatchers.IO).launch {
                    temperatureDao.insertTemp(Temperature(currentTemp = _currentTemp.value))
                }
            }
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TelemetryService.LocalBinder
            telemetryService = binder.service
            telemetryService?.setHvacStateListener(hvacStateListener)

            // Đồng bộ trạng thái ban đầu từ Engine qua TelemetryService
            _isHvacOn.value = telemetryService?.isHvacEnabled ?: false

            // Đọc nhiệt độ đã lưu từ DB, đẩy lại cho Engine
            CoroutineScope(Dispatchers.IO).launch {
                val savedTemp = temperatureDao.getCurrentTemp() ?: HvacConfig.DEFAULT_TEMPERATURE
                _currentTemp.value = savedTemp
                telemetryService?.setTargetTemperature(savedTemp)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            telemetryService?.setHvacStateListener(null)
            telemetryService = null
        }
    }


    override fun turnOnHvac() {
        telemetryService?.setHvacEnabled(true)
    }

    override fun turnOffHvac() {
        telemetryService?.setHvacEnabled(false)
    }

    override fun setTemperature(temp: Int) {
        telemetryService?.setTargetTemperature(temp)
        // Lưu DB ngay khi người dùng chỉnh — không chờ callback từ Engine
        CoroutineScope(Dispatchers.IO).launch {
            temperatureDao.insertTemp(Temperature(currentTemp = temp))
        }
    }

    override fun startAndBindService() {
        // TelemetryService đã được TelemetryRepositoryImpl khởi động trước
        // HVAC chỉ cần bind vào để lấy tham chiếu LocalBinder
        val intent = Intent(context, TelemetryService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun unbindService() {
        // Snapshot nhiệt độ cuối vào DB trước khi thoát
        CoroutineScope(Dispatchers.IO).launch {
            temperatureDao.insertTemp(Temperature(currentTemp = _currentTemp.value))
        }
        telemetryService?.setHvacStateListener(null)
        context.unbindService(serviceConnection)
        telemetryService = null
    }
}
