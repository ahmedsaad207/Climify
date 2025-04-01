package com.delighted2wins.climify.workers

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.delighted2wins.climify.service.WeatherUpdateService
import com.delighted2wins.climify.utils.Constants

class WeatherAlarmWorker(val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        Log.i("TAG", "doWork: ")

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//            setForegroundAsync(createForegroundInfo()) // Required for api 31+
//        }
//        setForeground(getForegroundInfo())

        val alarmJson = inputData.getString(Constants.KEY_ALARM)

        val serviceIntent = Intent(applicationContext, WeatherUpdateService::class.java)
        serviceIntent.putExtra(Constants.KEY_ALARM, alarmJson)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(applicationContext, serviceIntent)
        } else {
            applicationContext.startService(serviceIntent)
        }
        return Result.success()
    }

    /*private fun createForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(applicationContext, "weather_alert_channel")
            .setContentTitle("Weather Alert Scheduled")
            .setContentText("Next update coming soon. Stay prepared!")
            .setSmallIcon(R.drawable.ic_alarm)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        return ForegroundInfo(2, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
    }*/

    /*@RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = createNotification()
        return ForegroundInfo(2, notification)
    }*/

    /*@RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {
        val channelId = "weather_alerts"
        val channelName = "Weather Alerts"

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Weather Alarm")
            .setContentText("Fetching weather data...")
            .setSmallIcon(R.drawable.ic_alarm)
            .setOngoing(true) // Prevents swipe dismissal
            .build()
    }*/
}