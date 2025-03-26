package com.delighted2wins.climify.worker

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.delighted2wins.climify.R
import com.delighted2wins.climify.service.WeatherUpdateService
import com.delighted2wins.climify.utils.Constants

class WeatherAlarmWorker(val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setForegroundAsync(createForegroundInfo()) // Required for api 31+
        }

        val lat = inputData.getDouble(Constants.KEY_LAT, 0.0)
        val lon = inputData.getDouble(Constants.KEY_LON, 0.0)
        val tempUnit = inputData.getString(Constants.KEY_TEMP_UNIT) ?: "metric"
        val lang = inputData.getString(Constants.KEY_LANG) ?: "en"

        val serviceIntent = Intent(applicationContext, WeatherUpdateService::class.java)
        serviceIntent.putExtra(Constants.KEY_LAT, lat)
        serviceIntent.putExtra(Constants.KEY_LON, lon)
        serviceIntent.putExtra(Constants.KEY_TEMP_UNIT, tempUnit)
        serviceIntent.putExtra(Constants.KEY_LANG, lang)
        ContextCompat.startForegroundService(applicationContext, serviceIntent)
        return Result.success()
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(applicationContext, "weather_alerts_channel")
            .setContentTitle("Weather Alert Scheduled")
            .setContentText("Next update coming soon. Stay prepared!")
            .setSmallIcon(R.drawable.ic_alarm)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        return ForegroundInfo(2, notification)
    }
}