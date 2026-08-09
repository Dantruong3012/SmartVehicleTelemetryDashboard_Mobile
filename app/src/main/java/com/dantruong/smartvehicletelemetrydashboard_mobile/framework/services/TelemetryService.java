package com.dantruong.smartvehicletelemetrydashboard_mobile.framework.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;

import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.engine.TelemetryListener;
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.TelemetryData;

public class TelemetryService extends Service {
    private final IBinder binder = new LocalBinder();
    private TelemetryListener telemetryListener;
    private Thread simulatorThread;
    private boolean isRunning = false;

    // TODO: cần xem kỹ lại đoạn này
    public class LocalBinder extends Binder {
        public TelemetryService getService() {
            return TelemetryService.this;
        }
    }

    public void setTelemetryListener(TelemetryListener telemetryListener) {
        this.telemetryListener = telemetryListener;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(2, createNotification());
        startSimulatingData();
    }

    private void startSimulatingData(){
        if (isRunning) return;
        isRunning = true;
        simulatorThread = new Thread( () -> {
            int speed = 0;
            int battery = 100;
            boolean isAccelerating = true;
            while (isRunning){
                try {
                    if (isAccelerating) speed += 2; else speed -= 1;
                    if (speed >= 120) isAccelerating = false;
                    if (speed <= 0) isAccelerating = true;
                    if (battery > 0 && speed % 5 == 0) battery -= 1;
                    if (telemetryListener != null) {
                        telemetryListener.onTelemetryUpdated(new TelemetryData(speed, battery));
                    }
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        simulatorThread.start();
    }

    private Notification createNotification(){
        String channelId = "telemetry_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId,
                    "Monitoring background vehicle operation",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(notificationChannel);
        }
        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Vehicle is in operation")
                .setContentText("The measurement system is running in the background...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setOngoing(true)
                .build();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        if (simulatorThread != null) simulatorThread.interrupt();
    }
}
