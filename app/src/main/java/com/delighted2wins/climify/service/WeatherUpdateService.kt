package com.delighted2wins.climify.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.delighted2wins.climify.MainActivity
import com.delighted2wins.climify.R
import com.delighted2wins.climify.alarm.WeatherOverlay
import com.delighted2wins.climify.data.remote.RetrofitClient
import com.delighted2wins.climify.data.repo.WeatherRepository
import com.delighted2wins.climify.domainmodel.Alarm
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.enums.Language
import com.delighted2wins.climify.enums.LocationSource
import com.delighted2wins.climify.enums.TempUnit
import com.delighted2wins.climify.home.getRepo
import com.delighted2wins.climify.utils.Constants
import com.delighted2wins.climify.utils.getTempUnitSymbol
import com.delighted2wins.climify.utils.getUserLocationUsingGps
import com.delighted2wins.climify.utils.toCurrentWeather
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WeatherUpdateService : Service(), LifecycleOwner, SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView
    private lateinit var mediaPlayer: MediaPlayer
    private val lifecycleRegistry = LifecycleRegistry(this) // Create LifecycleRegistry
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private lateinit var rep: WeatherRepository

    override fun onCreate() { // api = 31
        super.onCreate()
        Log.i("TAG", "onCreate: service")
//        startForegroundService(createNotification())
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        rep = getRepo(this)
    }

    private fun startForegroundService(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 10 (API 34) and above
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(1, notification) // REQUIRED for Android 12+
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_SERVICE") {
            dismiss()
            return START_NOT_STICKY
        }
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED

        val lang = rep.getData<Language>(Constants.KEY_LANG).value
        val unit = rep.getData<TempUnit>(Constants.KEY_TEMP_UNIT).value
        val userLocation = rep.getData<LocationSource>(Constants.KEY_LOCATION_SOURCE).value

        val alarmJson = intent?.getStringExtra(Constants.KEY_ALARM)
        val alarm = Gson().fromJson(alarmJson, Alarm::class.java)
        val duration = alarm.endDuration

        if (alarm.type == Constants.TYPE_NOTIFICATION) {
//            startForeground(1, createNotification())
        } else {
//            startForeground(createNotification())
        }

        if (userLocation == LocationSource.MAP.value) {
            val (lat, lon) = rep.getData<Pair<Double, Double>>("")
            makeRequestAndShowNotification(lat, lon, unit, lang, alarm, duration)
        } else {
            getUserLocationUsingGps { lat, lon ->
                makeRequestAndShowNotification(lat, lon, unit, lang, alarm, duration)
            }
        }
        return START_STICKY
    }

    private fun makeRequestAndShowNotification(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String,
        alarm: Alarm,
        duration: Long
    ) {
        lifecycleScope.launch {
            try {
                val currentWeather = RetrofitClient.service.getCurrentWeather(
                    lat,
                    lon,
                    unit,
                    lang
                ).toCurrentWeather()
                startForegroundService(createNotification(currentWeather, alarm, unit))
                //                    showOverlayDialog(currentWeather)
            } catch (e: Exception) {
                startForegroundService(createNotification(null, alarm, unit))
                //showOverlayDialog(null)
            }
            playSound()
            delay(duration)
            dismiss()
        }
    }

    private fun playSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.song)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    private fun createNotification(
        currentWeather: CurrentWeather?,
        alarm: Alarm,
        unit: String
    ): Notification {
        val channelId = "weather_alert_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {   // 26+
            val channel = NotificationChannel(
                channelId,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description =
                    "Get timely weather updates, alerts, and reminders based on your location."
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

        val stopIntent = Intent(this, WeatherUpdateService::class.java).apply {
            action = "STOP_SERVICE"
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            val json = Gson().toJson(currentWeather)
            putExtra(Constants.KEY_CURRENT_WEATHER, json)
            putExtra(Constants.KEY_ALARM, Gson().toJson(alarm))
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val largeIconBitmap = getBitmapFromDrawable(this, currentWeather?.icon ?: R.drawable._3d)


        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Weather in ${currentWeather?.city ?: "null"}")
            .setContentText("${currentWeather?.temp ?: "0"}${getTempUnitSymbol(unit)} today - ${currentWeather?.description ?: " null "} - See full forecast")
            .setSmallIcon(R.drawable.ic_alarm)
            .setLargeIcon(largeIconBitmap)
            .setContentIntent(openAppPendingIntent)
            .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
            .addAction(R.drawable.ic_open_in_new, "See forecast", openAppPendingIntent)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build().apply {
                flags = Notification.FLAG_NO_CLEAR
            }
    }

    private fun showOverlayDialog(currentWeather: CurrentWeather?) {

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Window Configuration
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) // api >= 26
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP

        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@WeatherUpdateService)
            setViewTreeSavedStateRegistryOwner(this@WeatherUpdateService)
            setContent {
                WeatherOverlay(currentWeather) { dismiss() }
            }
        }

        windowManager.addView(composeView, params)
    }

    private fun dismiss() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true) // Older versions
        }
        stopSelf()
    }

    override fun onDestroy() {
        Log.i("TAG", "onDestroy: ")
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        if (::composeView.isInitialized && composeView.isAttachedToWindow) {
            windowManager.removeView(composeView)
        }
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry
}

fun getBitmapFromDrawable(context: Context, drawableId: Int): Bitmap? {
    val drawable = AppCompatResources.getDrawable(context, drawableId) ?: return null
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}