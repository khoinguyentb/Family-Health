package com.kan.dev.familyhealth.ui.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.databinding.ActivityExerciseBinding
import com.kan.dev.familyhealth.utils.showPermissionSettingsDialog

class ExerciseActivity : BaseActivity<ActivityExerciseBinding>(), SensorEventListener {
    private val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val sensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }
    private var stepCount = 0
    private var distanceInMeters = 0.0
    private var distanceInKm = 0.0
    private var caloriesBurned = 0.0
    private val RECOGNITION_REQUEST_CODE = 100

    override fun setViewBinding(): ActivityExerciseBinding {
        return ActivityExerciseBinding.inflate(layoutInflater)
    }

    override fun initData() {
        Log.d("KanMobile",sensor.toString())
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)
        for (sensor in sensorList) {
            Log.d("Sensor", sensor.name)
        }
    }

    override fun initView() {

    }

    override fun initListener() {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        binding.apply {
            if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                stepCount = event.values[0].toInt()
                tvStep.text = stepCount.toString()
                distanceInMeters = stepCount * 0.762
                distanceInKm = distanceInMeters / 1000
                tvDistance.text = "%.2f km".format(distanceInKm)

                caloriesBurned = stepCount * 0.05
                tvMoving.text = "%.2f".format(caloriesBurned)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Xử lý khi độ chính xác của sensor thay đổi (nếu cần thiết)
    }

    override fun onResume() {
        super.onResume()
        requestActivityRecognitionPermission()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private fun requestActivityRecognitionPermission() {
        val permission = Manifest.permission.ACTIVITY_RECOGNITION
        val requestCode = RECOGNITION_REQUEST_CODE
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            startStepCounter()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECOGNITION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startStepCounter()
            } else {
                showPermissionSettingsDialog()
            }
        }
    }

    private fun startStepCounter() {
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            binding.tvStep.text = getString(R.string.noStep)
        }
    }
}