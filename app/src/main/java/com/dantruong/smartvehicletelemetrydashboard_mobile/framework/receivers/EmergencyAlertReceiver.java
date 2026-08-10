package com.dantruong.smartvehicletelemetrydashboard_mobile.framework.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.dantruong.smartvehicletelemetrydashboard_mobile.R;

public class EmergencyAlertReceiver extends BroadcastReceiver {
    public static final String ACTION_EMERGENCY_ALERT =
            "com.dantruong.smartvehicletelemetrydashboard_mobile.EMERGENCY_ALERT";
    public static final String EXTRA_ALERT_TYPE = "extra_alert_type";
    public static final String EXTRA_ALERT_MESSAGE = "extra_alert_message";

    private static final String ALERT_CHANNEL_ID = "vehicle_emergency_alerts";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !ACTION_EMERGENCY_ALERT.equals(intent.getAction())) return;

        String type = intent.getStringExtra(EXTRA_ALERT_TYPE);
        String message = intent.getStringExtra(EXTRA_ALERT_MESSAGE);
        if (message == null || message.isEmpty()) {
            message = "Vehicle emergency alert";
        }

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    ALERT_CHANNEL_ID,
                    "Vehicle emergency alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(type == null ? "Emergency Alert" : type)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
