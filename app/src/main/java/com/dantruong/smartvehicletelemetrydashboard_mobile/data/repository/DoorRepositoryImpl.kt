package com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.dantruong.smartvehicletelemetrydashboard_mobile.IDoorControlCallback
import com.dantruong.smartvehicletelemetrydashboard_mobile.IDoorControlInterface
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.DoorStateListener
import com.dantruong.smartvehicletelemetrydashboard_mobile.framework.services.DoorControlService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DoorRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DoorRepository {

    private var doorService: IDoorControlInterface? = null
    private var isBound = false
    private val listeners = CopyOnWriteArrayList<DoorStateListener>()

    private val doorCallback = object : IDoorControlCallback.Stub() {
        override fun onDoorStateChanged(doorId: Int, isOpen: Boolean) {
            listeners.forEach { it.onDoorStateChanged(doorId, isOpen) }
        }

        override fun onDoorError(doorId: Int, errorCode: Int) {
            Log.e("DoorRepository", "Door Error: $doorId - code: $errorCode")
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            doorService = IDoorControlInterface.Stub.asInterface(service)
            isBound = true
            try {
                doorService?.registerCallback(doorCallback)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            doorService = null
            isBound = false
        }
    }

    init {
        val intent = Intent(context, DoorControlService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun requestOpenDoor(doorId: Int) {
        try {
            doorService?.requestOpenDoor(doorId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun requestCloseDoor(doorId: Int) {
        try {
            doorService?.requestCloseDoor(doorId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun isDoorOpen(doorId: Int): Boolean {
        return try {
            doorService?.isDoorOpen(doorId) ?: false
        } catch (e: Exception) {
            false
        }
    }

    override fun registerListener(listener: DoorStateListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    override fun unregisterListener(listener: DoorStateListener) {
        listeners.remove(listener)
    }

    override fun unbindService() {
        if (isBound) {
            try {
                doorService?.unregisterCallback(doorCallback)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            context.unbindService(serviceConnection)
            isBound = false
        }
        doorService = null
    }
}
