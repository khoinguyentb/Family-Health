package com.kan.dev.familyhealth.ui.activity.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.RealtimeDAO
import com.kan.dev.familyhealth.data.model.BMI
import com.kan.dev.familyhealth.data.model.FriendModel
import com.kan.dev.familyhealth.data.model.HealthyModel
import com.kan.dev.familyhealth.databinding.ActivityMainBinding
import com.kan.dev.familyhealth.service.InternetBroadcastReceiver
import com.kan.dev.familyhealth.service.LocationUpdateService
import com.kan.dev.familyhealth.service.LocationUpdateService.Companion.isServiceRunning
import com.kan.dev.familyhealth.ui.activity.BMI.BMIActivity
import com.kan.dev.familyhealth.ui.activity.ExerciseActivity
import com.kan.dev.familyhealth.ui.activity.gps.GPSActivity
import com.kan.dev.familyhealth.utils.Code
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.checkPermissionRECOGNITION
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick
import com.kan.dev.familyhealth.utils.requestAppPermissionRECOGNITION
import com.kan.dev.familyhealth.utils.showPermissionSettingsDialog
import com.kan.dev.familyhealth.viewmodel.FriendViewModel
import com.kan.dev.familyhealth.viewmodel.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.util.HashMap

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun setViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var receiver: InternetBroadcastReceiver
    private val viewModel : FriendViewModel by viewModels()
    private lateinit var myCode : String
    private var runnable: Runnable? = null
    private val viewModelSign : SignInViewModel by viewModels()
    private var avtId = 0
    private var battery = 0
    private var name = ""
    private var gender = ""
    private var phoneNumber = ""
    private var latLng = ""
    private var lastActive = 0L
    private var isTracking = true
    private var isSos = true
    private var weight = 0f
    private var height = 0f
    private val RECOGNITION_REQUEST_CODE = 100
    override fun initData() {
        receiver = InternetBroadcastReceiver()
        myCode = sharePre.getString(MY_CODE,"")!!
        RealtimeDAO.initRealtimeData()
        try {
            RealtimeDAO.getRealtimeData("$myCode/healthys") { snapshot ->
                viewModelSign.deleteAllHealthy()
                for (dataSnapshot in snapshot!!.children) {
                    val model = dataSnapshot.getValue(HealthyModel::class.java)
                    model?.let {
                        viewModelSign.insertHealthy(model)
                        Log.d("KanMobile","Kan")
                    }
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

        try {
            RealtimeDAO.getRealtimeData("$myCode/BMI") { snapshot ->
                viewModelSign.deleteAllBMI()
                for (dataSnapshot in snapshot!!.children) {
                    val model = dataSnapshot.getValue(BMI::class.java)
                    model?.let {
                        viewModelSign.insertBMI(model)
                        Log.d("KanMobile","KanBMI")
                    }
                }
            }

        }catch (e : Exception){
            e.printStackTrace()
        }

        try {
            RealtimeDAO.getRealtimeData("$myCode/friends") { snapshot ->
                viewModelSign.deleteAllFriend()
                for (dataSnapshot in snapshot!!.children) {
                    val model = dataSnapshot.getValue(FriendModel::class.java)
                    model?.let {
                        viewModelSign.insertFriend(model)
                        Log.d("KanMobile","KanFriend")
                    }
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

        runnable = object : Runnable {
            override fun run() {
                getFriend()
                handler.postDelayed(this, 15000)
            }
        }
        handler.postDelayed(runnable!!, 15000)
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
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStart() {
        super.onStart()
        if (!checkPermissionRECOGNITION()){
            requestAppPermissionRECOGNITION(RECOGNITION_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceRunning(this)) {
            val serviceIntent = Intent(this, LocationUpdateService::class.java)
            stopService(serviceIntent)
        }
        handler.removeCallbacks(runnable!!)
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

    private fun getFriend() {
        viewModel.getAll.observe(this){
            for (i in it) {
                RealtimeDAO.getOnetimeData(
                    myCode + "/friends/" + i.code
                ) { snapshot1 ->
                    try {
                        val visible =
                            snapshot1!!.child("visible").getValue(Boolean::class.java)!!
                        if (visible) {
                            RealtimeDAO.getOnetimeData(i.code) { snapshot ->
                                avtId = snapshot!!.child("avt").getValue(Int::class.java)!!
                                battery = snapshot.child("battery").getValue(Int::class.java)!!
                                name = snapshot.child("name").getValue(String::class.java)!!
                                gender = snapshot.child("gender").getValue(String::class.java)!!
                                phoneNumber = snapshot.child("phoneNumber").getValue(String::class.java)!!
                                latLng = snapshot.child("latLng").getValue(String::class.java)!!
                                lastActive = snapshot.child("lastActive").getValue(Long::class.java)!!
                                isTracking = snapshot.child("isTracking").getValue(Boolean::class.java)!!
                                isSos = snapshot.child("isSos").getValue(Boolean::class.java)!!
                                weight = snapshot.child("weight").getValue(Float::class.java)!!
                                height = snapshot.child("height").getValue(Float::class.java)!!

                                if (isTracking) {
                                    i.avt = avtId
                                    i.battery = battery
                                    i.name = name
                                    i.gender = gender
                                    i.phoneNumber = phoneNumber
                                    i.latLng = latLng
                                    i.lastActive = lastActive
                                    i.isSos = isSos
                                    i.weight = weight
                                    i.height = height
                                    viewModel.update(i)

                                    val friend: MutableMap<String, Any> =
                                        HashMap()
                                    friend["battery"] = battery
                                    friend["name"] = name
                                    friend["avt"] = avtId
                                    friend["phoneNumber"] = phoneNumber
                                    friend["gender"] = gender
                                    friend["isTracking"] = isTracking
                                    friend["isSos"] = isSos
                                    friend["lastActive"] = lastActive
                                    friend["latLng"] = latLng
                                    friend["weight"] = weight
                                    friend["height"] = height

                                    RealtimeDAO.updateRealtimeData(
                                        myCode + "/friends/" + snapshot.key,
                                        friend
                                    ) { _ -> }
                                } else {
                                    val friend: MutableMap<String, Any> =
                                        HashMap()
                                    friend["isTracking"] = isTracking
                                    RealtimeDAO.updateRealtimeData(
                                        myCode + "/friends/" + snapshot.key,
                                        friend
                                    ) { _ -> }
                                }
                            }
                        }
                    } catch (_: Exception) {

                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            RECOGNITION_REQUEST_CODE ->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    onStart()
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION)) {
                        showPermissionSettingsDialog()
                    }
                }
            }
        }
    }
}