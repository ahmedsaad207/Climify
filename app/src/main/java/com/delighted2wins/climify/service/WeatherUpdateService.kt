package com.delighted2wins.climify.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
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
import com.delighted2wins.climify.R
import com.delighted2wins.climify.alarm.WeatherOverlay
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.home.getRepo
import com.delighted2wins.climify.utils.Constants
import com.delighted2wins.climify.utils.toCurrentWeather
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WeatherUpdateService : Service(), LifecycleOwner, SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView
    private val lifecycleRegistry = LifecycleRegistry(this) // Create LifecycleRegistry
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    //    var currentWeather: CurrentWeather? = null
    override fun onCreate() {
        Log.i("TAG", "service: create")
        super.onCreate()
        startForeground(createNotification())
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    private fun startForeground(notification: Notification) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 10 (API 34) and above
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(1, notification) // REQUIRED for Android 12+
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED

        val lat = intent?.getDoubleExtra(Constants.KEY_LAT, 0.0)
        val lon = intent?.getDoubleExtra(Constants.KEY_LON, 0.0)
        val lang = intent?.getStringExtra(Constants.KEY_LANG) ?: "en"
        val tempUnit = intent?.getStringExtra(Constants.KEY_TEMP_UNIT) ?: "metric"

        if (lat != null && lon != null) {
            lifecycleScope.launch {
                try {
                    val currentWeather = getRepo(this@WeatherUpdateService).getCurrentWeather(
                        lat,
                        lon,
                        tempUnit,
                        lang
                    )
                        .map { weather ->
                            weather.toCurrentWeather()
                        }
                        .firstOrNull()

                    showOverlayDialog(currentWeather)
                } catch (e: Exception) {
                    showOverlayDialog(null)
                }
            }
        } else {
            showOverlayDialog(null)
        }

        return START_STICKY
    }

    private fun createNotification(): Notification {
        val channelId = "weather_alert_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {   // 26+
            val channel = NotificationChannel(
                channelId,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description =
                    "Get timely weather updates, alerts, and reminders based on your location."
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Weather Alerts")
            .setContentText("Running in the background")
            .setSmallIcon(R.drawable.ic_alarm)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
        return notification
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
                WeatherOverlay(currentWeather) { dismissOverlay() }
            }
        }

        windowManager.addView(composeView, params)
    }

    private fun dismissOverlay() {
        windowManager.removeView(composeView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true) // Older versions
        }
        stopSelf()
    }

    override fun onDestroy() {
        Log.i("TAG", "service: onDestroy")
        super.onDestroy()

        if (::composeView.isInitialized && composeView.isAttachedToWindow) {
            windowManager.removeView(composeView)
        }
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED  // 🔹 Destroy lifecycle
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry
}