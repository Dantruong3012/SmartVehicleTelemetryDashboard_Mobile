package com.dantruong.smartvehicletelemetrydashboard_mobile.framework.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.dantruong.smartvehicletelemetrydashboard_mobile.ICanbusCallBack;
import com.dantruong.smartvehicletelemetrydashboard_mobile.ICanbusInterface;
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.HvacStateListener;
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.TelemetryListener;
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.TelemetryData;


public class TelemetryService extends Service {

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public TelemetryService getService() {
            return TelemetryService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private TelemetryListener telemetryListener;
    private HvacStateListener hvacStateListener;

    public void setTelemetryListener(TelemetryListener listener) {
        this.telemetryListener = listener;
    }

    public void setHvacStateListener(HvacStateListener listener) {
        this.hvacStateListener = listener;
    }

    private ICanbusInterface hvacEngineService;

    private final ICanbusCallBack hvacCallback = new ICanbusCallBack.Stub() {
        @Override
        public void onTemperatureChanged(int temp) {
            if (hvacStateListener != null) {
                hvacStateListener.onTemperatureChanged(temp);
            }
        }

        @Override
        public void onTurnHvacEngine(boolean isOn) {
            if (hvacStateListener != null) {
                hvacStateListener.onHvacStateChanged(isOn);
            }
        }
    };

    private final ServiceConnection hvacConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            hvacEngineService = ICanbusInterface.Stub.asInterface(service);
            try {
                hvacEngineService.registerCallBack(hvacCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            hvacEngineService = null;
        }
    };

    private void bindToHvacEngine() {
        Intent intent = new Intent(this, HvacEngineService.class);
        startService(intent);
        bindService(intent, hvacConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindFromHvacEngine() {
        if (hvacEngineService != null) {
            try {
                hvacEngineService.unRegisterCallBack(hvacCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
            unbindService(hvacConnection);
            hvacEngineService = null;
        }
    }


    public void setHvacEnabled(boolean enabled) {
        if (hvacEngineService == null) return;
        try {
            hvacEngineService.setHvacEnabled(enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTargetTemperature(int temp) {
        if (hvacEngineService == null) return;
        try {
            hvacEngineService.setTargetTemperature(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isHvacEnabled() {
        if (hvacEngineService == null) return false;
        try {
            return hvacEngineService.isHvacEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    private Thread simulatorThread;
    private boolean isRunning = false;
    private volatile boolean isEmergencyBraking = false;

    public void setEmergencyBraking(boolean emergencyBraking) {
        this.isEmergencyBraking = emergencyBraking;
    }

    private void startSimulatingData() {
        if (isRunning) return;
        isRunning = true;
        simulatorThread = new Thread(() -> {
            int speed = 0;
            int battery = 100;
            boolean isAccelerating = true;
            while (isRunning) {
                try {
                    if (isEmergencyBraking) {
                        if (speed > 0) speed -= 10;
                        if (speed < 0) speed = 0;
                    } else {
                        if (isAccelerating) speed += 2; else speed -= 1;
                        if (speed >= 120) isAccelerating = false;
                        if (speed <= 0) isAccelerating = true;
                    }
                    int finalSpeed = speed;
                    if (battery > 0 && speed % 5 == 0) battery -= 1;
                    int finalBattery = battery;
                    if (telemetryListener != null) {
                        telemetryListener.onTelemetryUpdated(new TelemetryData(finalSpeed, finalBattery));
                    }
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        simulatorThread.start();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(2, createNotification());
        bindToHvacEngine();
        startSimulatingData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        if (simulatorThread != null) simulatorThread.interrupt();
        unbindFromHvacEngine();
    }


    private Notification createNotification() {
        String channelId = "telemetry_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Monitoring background vehicle operation",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Vehicle is in operation")
                .setContentText("The measurement system is running in the background...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setOngoing(true)
                .build();
    }
}
