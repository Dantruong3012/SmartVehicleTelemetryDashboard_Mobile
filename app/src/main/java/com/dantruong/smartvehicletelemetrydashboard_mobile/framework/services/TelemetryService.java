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
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.AppDatabase;
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.entity.AlertLog;
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.local.entity.TripLog;
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.HvacStateListener;
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.TelemetryListener;
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.TelemetryData;
import com.dantruong.smartvehicletelemetrydashboard_mobile.framework.receivers.EmergencyAlertReceiver;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TelemetryService extends Service {
    private static final int LOW_BATTERY_THRESHOLD = 10;
    private static final int OVERHEAT_THRESHOLD = 100;
    private static final int TELEMETRY_INTERVAL_MS = 200;
    private static final int LOG_EVERY_TICKS = 5;

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
    @Inject
    AppDatabase appDatabase;

    public void setTelemetryListener(TelemetryListener listener) {
        this.telemetryListener = listener;
    }

    public void setHvacStateListener(HvacStateListener listener) {
        this.hvacStateListener = listener;
    }

    private ICanbusInterface hvacEngineService;
    private Boolean pendingHvacEnabled;
    private Integer pendingTargetTemperature;

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
                if (pendingTargetTemperature != null) {
                    hvacEngineService.setTargetTemperature(pendingTargetTemperature);
                    pendingTargetTemperature = null;
                }
                if (pendingHvacEnabled != null) {
                    hvacEngineService.setHvacEnabled(pendingHvacEnabled);
                    pendingHvacEnabled = null;
                }
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
        pendingHvacEnabled = enabled;
        if (hvacEngineService == null) return;
        try {
            hvacEngineService.setHvacEnabled(enabled);
            pendingHvacEnabled = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTargetTemperature(int temp) {
        pendingTargetTemperature = temp;
        if (hvacEngineService == null) return;
        try {
            hvacEngineService.setTargetTemperature(temp);
            pendingTargetTemperature = null;
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
    private volatile boolean isRunning = false;
    private boolean lowBatteryAlertSent = false;
    private boolean overheatAlertSent = false;

    private void startSimulatingData() {
        if (isRunning) return;
        isRunning = true;
        simulatorThread = new Thread(() -> {
            int speed = 0;
            int battery = 100;
            int engineTemperature = 72;
            int tick = 0;
            boolean isAccelerating = true;
            while (isRunning) {
                try {
                    if (isAccelerating) speed += 2; else speed -= 1;
                    if (speed >= 120) isAccelerating = false;
                    if (speed <= 0) isAccelerating = true;
                    if (battery > 0 && speed % 5 == 0) battery -= 1;

                    if (speed > 85) {
                        engineTemperature += 2;
                    } else if (speed < 35 && engineTemperature > 70) {
                        engineTemperature -= 1;
                    }
                    if (engineTemperature > 112) engineTemperature = 86;

                    int finalSpeed = speed;
                    int finalBattery = battery;
                    int finalEngineTemperature = engineTemperature;
                    TelemetryData telemetryData =
                            new TelemetryData(finalSpeed, finalBattery, finalEngineTemperature);

                    if (telemetryListener != null) {
                        telemetryListener.onTelemetryUpdated(telemetryData);
                    }

                    tick++;
                    if (tick % LOG_EVERY_TICKS == 0) {
                        persistTripLog(telemetryData);
                    }

                    evaluateEmergencyAlerts(telemetryData);
                    Thread.sleep(TELEMETRY_INTERVAL_MS);
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

    private void persistTripLog(TelemetryData data) {
        if (appDatabase == null) return;
        appDatabase.tripLogDao().insertTripLog(new TripLog(
                0,
                data.speed,
                data.batteryLevel,
                data.engineTemperature,
                System.currentTimeMillis()
        ));
    }

    private void evaluateEmergencyAlerts(TelemetryData data) {
        if (data.batteryLevel <= LOW_BATTERY_THRESHOLD && !lowBatteryAlertSent) {
            lowBatteryAlertSent = true;
            sendEmergencyAlert(
                    "LOW_BATTERY",
                    "Bíp bíp: Xe sắp hết pin (" + data.batteryLevel + "%).",
                    data
            );
        }

        if (data.engineTemperature > OVERHEAT_THRESHOLD && !overheatAlertSent) {
            overheatAlertSent = true;
            sendEmergencyAlert(
                    "ENGINE_OVERHEAT",
                    "Cảnh báo: Nhiệt độ động cơ quá cao (" + data.engineTemperature + "°C).",
                    data
            );
        }

        if (data.batteryLevel > LOW_BATTERY_THRESHOLD + 5) {
            lowBatteryAlertSent = false;
        }
        if (data.engineTemperature < OVERHEAT_THRESHOLD - 10) {
            overheatAlertSent = false;
        }
    }

    private void sendEmergencyAlert(String type, String message, TelemetryData data) {
        if (appDatabase != null) {
            appDatabase.alertLogDao().insertAlertLog(new AlertLog(
                    0,
                    type,
                    message,
                    data.speed,
                    data.batteryLevel,
                    data.engineTemperature,
                    System.currentTimeMillis()
            ));
        }

        Intent alertIntent = new Intent(this, EmergencyAlertReceiver.class);
        alertIntent.setAction(EmergencyAlertReceiver.ACTION_EMERGENCY_ALERT);
        alertIntent.putExtra(EmergencyAlertReceiver.EXTRA_ALERT_TYPE, type);
        alertIntent.putExtra(EmergencyAlertReceiver.EXTRA_ALERT_MESSAGE, message);
        sendBroadcast(alertIntent);
    }


    private Notification createNotification() {
        String channelId = "telemetry_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Giám sát xe nền",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Đang kết nối xe...")
                .setContentText("Telemetry Service Running")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setOngoing(true)
                .build();
    }
}
