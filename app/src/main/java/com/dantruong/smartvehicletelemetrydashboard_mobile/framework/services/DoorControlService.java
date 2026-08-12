package com.dantruong.smartvehicletelemetrydashboard_mobile.framework.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.dantruong.smartvehicletelemetrydashboard_mobile.IDoorControlCallback;
import com.dantruong.smartvehicletelemetrydashboard_mobile.IDoorControlInterface;
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.DoorEngineListener;
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.MockDoorEngine;

import java.util.concurrent.CopyOnWriteArrayList;

public class DoorControlService extends Service {
    private MockDoorEngine mockDoorEngine;
    private final CopyOnWriteArrayList<IDoorControlCallback> callbacks = new CopyOnWriteArrayList<>();
    
    private HandlerThread ipcHandlerThread;
    private Handler ipcHandler;

    private TelemetryService telemetryService;
    private boolean isBound = false;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            TelemetryService.LocalBinder binder = (TelemetryService.LocalBinder) service;
            telemetryService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    private final IDoorControlInterface.Stub binder = new IDoorControlInterface.Stub() {
        @Override
        public void requestOpenDoor(int doorId) throws RemoteException {
            if (mockDoorEngine != null) mockDoorEngine.requestOpenDoor(doorId);
        }

        @Override
        public void requestCloseDoor(int doorId) throws RemoteException {
            if (mockDoorEngine != null) mockDoorEngine.requestCloseDoor(doorId);
        }

        @Override
        public boolean isDoorOpen(int doorId) throws RemoteException {
            return mockDoorEngine != null && mockDoorEngine.isDoorOpen(doorId);
        }

        @Override
        public void registerCallback(IDoorControlCallback callback) throws RemoteException {
            if (callback != null) callbacks.add(callback);
        }

        @Override
        public void unregisterCallback(IDoorControlCallback callback) throws RemoteException {
            if (callback != null) callbacks.remove(callback);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize dedicated IPC dispatch thread
        ipcHandlerThread = new HandlerThread("DoorIpcHandlerThread");
        ipcHandlerThread.start();
        ipcHandler = new Handler(ipcHandlerThread.getLooper());

        mockDoorEngine = new MockDoorEngine();
        mockDoorEngine.setListener(new DoorEngineListener() {
            @Override
            public void onDoorStateChanged(int doorId, boolean isOpen) {
                // Post AIDL callback task to IPC thread so it does not block Engine thread
                ipcHandler.post(() -> {
                    for (IDoorControlCallback callback : callbacks) {
                        try {
                            callback.onDoorStateChanged(doorId, isOpen);
                        } catch (RemoteException e) {
                            callbacks.remove(callback);
                        }
                    }
                });
            }

            @Override
            public void onEmergencyBrakeTriggered(boolean isBraking) {
                // Local communication with TelemetryService
                if (isBound && telemetryService != null) {
                    telemetryService.setEmergencyBraking(isBraking);
                }
            }
        });

        Intent intent = new Intent(this, TelemetryService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBound) unbindService(connection);
        if (mockDoorEngine != null) mockDoorEngine.destroy();
        if (ipcHandlerThread != null) ipcHandlerThread.quit();
    }
}
