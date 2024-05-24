package com.kan.dev.familyhealth.ui.activity.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.databinding.ActivityMainBinding
import com.kan.dev.familyhealth.service.InternetBroadcastReceiver
import com.kan.dev.familyhealth.service.LocationUpdateService
import com.kan.dev.familyhealth.service.LocationUpdateService.Companion.isServiceRunning
import com.kan.dev.familyhealth.ui.activity.BMI.BMIActivity
import com.kan.dev.familyhealth.ui.activity.ExerciseActivity
import com.kan.dev.familyhealth.ui.activity.gps.GPSActivity
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun setViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var receiver: InternetBroadcastReceiver
    override fun initData() {
        receiver = InternetBroadcastReceiver()
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun initView() {
        if (!isServiceRunning(this)) {
            val serviceIntent = Intent(this, LocationUpdateService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            }
        }
        registerReceiver()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (isServiceRunning(this)) {
            val serviceIntent = Intent(this, LocationUpdateService::class.java)
            stopService(serviceIntent)
        }
        unregisterReceiver()
    }

    override fun initListener() {
        binding.apply {
            btnExercise.setOnClickListener {
                startActivity(Intent(this@MainActivity, ExerciseActivity::class.java))
            }
            btnBMI.setOnClickListener {
                startActivity(Intent(this@MainActivity, BMIActivity::class.java))
            }
            btnMap.setOnClickListener {
                startActivity(Intent(this@MainActivity, GPSActivity ::class.java))
            }
            profileImage.setOnClickListener {
                if (isClick){
                    isClick = false
                    startActivity(Intent(this@MainActivity, SettingActivity::class.java))
                    handler.postDelayed({ isClick = true},500)
                }
            }
        }
    }

    private fun registerReceiver() {
        if (!InternetBroadcastReceiver.isRegistered) {
            val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            registerReceiver(receiver, filter)
            InternetBroadcastReceiver.isRegistered = true
        }
    }

    private fun unregisterReceiver() {
        if (InternetBroadcastReceiver.isRegistered) {
            unregisterReceiver(receiver)
            InternetBroadcastReceiver.isRegistered = false
        }
    }
}