package com.dantruong.smartvehicletelemetrydashboard_mobile.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dantruong.smartvehicletelemetrydashboard_mobile.data.repository.WeatherRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WeatherSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val weatherRepository: WeatherRepository
) : CoroutineWorker(context, workerParams) {

    private val CHANNEL_ID = "weather_sync_channel"
    private val NOTIFICATION_ID = 2001

    override suspend fun doWork(): Result {
        showFetchingNotification()
        return try {
            val result = weatherRepository.getWeatherData()
            if (result.isSuccess) {
                cancelNotification()
                Result.success()
            } else {
                cancelNotification()
                Result.retry()
            }
        } catch (e: Exception) {
            cancelNotification()
            Result.retry()
        }
    }

    private fun showFetchingNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Weather Sync",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Smart Vehicle")
            .setContentText("Fetching latest weather data...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun cancelNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
