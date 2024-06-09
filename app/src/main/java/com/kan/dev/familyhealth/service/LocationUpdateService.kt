package com.kan.dev.familyhealth.service

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.data.RealtimeDAO.initRealtimeData
import com.kan.dev.familyhealth.data.RealtimeDAO.updateRealtimeData
import com.kan.dev.familyhealth.utils.LocationHelper.getCurrentLocation
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.SharePreferencesUtils
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.viewmodel.HealthyViewModel
import com.kan.dev.familyhealth.viewmodel.HealthyViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
@SuppressLint("ObsoleteSdkInt")
class LocationUpdateService : Service() {
    private var runnable: Runnable? = null

    private var battery = 0
    private var lastActive = 0L
    private var latlngObj = mutableMapOf<String, Any>()
    @Inject
    lateinit var sharePre : SharePreferencesUtils
    private lateinit var myCode : String

    override fun onCreate() {
        super.onCreate()
        initRealtimeData()

        myCode = sharePre.getString(MY_CODE,"")!!
        runnable = object : Runnable {
            override fun run() {
                getCurrentLocation(applicationContext) { latLng ->
                    Log.e("Kano", myCode)
                    battery = batteryLevel
                    lastActive = System.currentTimeMillis()

                    latlngObj["latLng"] = latLng.toString()
                    latlngObj["battery"] = battery
                    latlngObj["lastActive"] = lastActive
                    updateRealtimeData(myCode, latlngObj) { _ -> }
                }
                handler.postDelayed(this, 10000)
            }
        }
    }
    @get:RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private val batteryLevel: Int
        get() {
            val batteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager
            return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: -1
        }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            // Xử lý trường hợp intent bị null
            Log.e("LocationUpdateService", "Intent is null in onStartCommand")
            return START_NOT_STICKY
        }

        handler.postDelayed(runnable!!, 10000)
        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable!!)
    }

    @SuppressLint("ObsoleteSdkInt")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotification(): Notification {
        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        val channel =
            NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "channel_id")
            .setContentTitle(getString(R.string.app_name))
            .setContentText("")
            .setSmallIcon(R.drawable.logo)
        return builder.build()
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    companion object {
        private const val NOTIFICATION_ID = 123
        fun isServiceRunning(context: Context?): Boolean {
            if (context == null) return false
            val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            for (serviceInfo in activityManager.getRunningServices(Int.MAX_VALUE)) {
                if (LocationUpdateService::class.java.name.equals(
                        serviceInfo.service.className,
                        ignoreCase = true
                    )
                ) return true
            }
            return false
        }
    }

}

