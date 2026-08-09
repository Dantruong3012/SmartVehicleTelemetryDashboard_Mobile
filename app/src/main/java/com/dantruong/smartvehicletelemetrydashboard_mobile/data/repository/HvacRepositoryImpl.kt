package com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.dantruong.smartvehicletelemetrydashboard_mobile.ICanbusCallBack
import com.dantruong.smartvehicletelemetrydashboard_mobile.ICanbusInterface
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.dao.TemperatureDao
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.entity.Temperature
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.HvacConfig
import com.dantruong.smartvehicletelemetrydashboard_mobile.framework.services.HvacEngineService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Singleton
class HvacRepositoryImpl
@Inject
constructor(
        @ApplicationContext private val context: Context,
        private val temperatureDao: TemperatureDao
) : HvacRepository {

    private var hvacService: ICanbusInterface? = null

    private val _currentTemp = MutableStateFlow(HvacConfig.DEFAULT_TEMPERATURE)
    override val currentTemp: StateFlow<Int> = _currentTemp.asStateFlow()

    private val _isHvacOn = MutableStateFlow(false)
    override val isHvacOn: StateFlow<Boolean> = _isHvacOn.asStateFlow()

    private val callback =
            object : ICanbusCallBack.Stub() {
                override fun onTemperatureChanged(temp: Int) {
                    if (_currentTemp.value != temp) {
                        _currentTemp.value = temp
                        CoroutineScope(Dispatchers.IO).launch {
                            temperatureDao.insertTemp(Temperature(currentTemp = temp))
                        }
                    }
                }

                override fun onTurnHvacEngine(isOn: Boolean) {
                    _isHvacOn.value = isOn
                    if (!isOn) {
                        CoroutineScope(Dispatchers.IO).launch {
                            temperatureDao.insertTemp(Temperature(currentTemp = _currentTemp.value))
                        }
                    }
                }
            }

    private val serviceConnection =
            object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    hvacService = ICanbusInterface.Stub.asInterface(service)
                    hvacService?.registerCallBack(callback)
                    hvacService?.let { _isHvacOn.value = it.isHvacEnabled }

                    // Đọc nhiệt độ đã lưu từ DB, set lại cho engine
                    // để tránh bị reset về DEFAULT khi khởi động lại app
                    CoroutineScope(Dispatchers.IO).launch {
                        val savedTemp = temperatureDao.getCurrentTemp() ?: HvacConfig.DEFAULT_TEMPERATURE
                        _currentTemp.value = savedTemp
                        hvacService?.setTargetTemperature(savedTemp)
                    }
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    hvacService?.unRegisterCallBack(callback)
                    hvacService = null
                }
            }

    override fun turnOnHvac() {
        hvacService?.setHvacEnabled(true)
    }

    override fun turnOffHvac() {
        hvacService?.setHvacEnabled(false)
    }

    override fun setTemperature(temp: Int) {
        hvacService?.setTargetTemperature(temp)
    }

    override fun startAndBindService() {
        val intent = Intent(context, HvacEngineService::class.java)
        context.startService(intent)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun unbindService() {
        // Snapshot nhiệt độ hiện tại vào DB trước khi thoát
        // Phòng trường hợp engine chưa kịp bắn callback cuối
        CoroutineScope(Dispatchers.IO).launch {
            temperatureDao.insertTemp(Temperature(currentTemp = _currentTemp.value))
        }
        hvacService?.unRegisterCallBack(callback)
        context.unbindService(serviceConnection)
        hvacService = null
    }
}
