package com.kan.dev.familyhealth.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.model.HealthyModel
import com.kan.dev.familyhealth.databinding.ActivityExerciseBinding
import com.kan.dev.familyhealth.utils.showPermissionSettingsDialog
import com.kan.dev.familyhealth.viewmodel.HealthyViewModel
import com.kan.dev.familyhealth.viewmodel.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ExerciseActivity : BaseActivity<ActivityExerciseBinding>() {

    private var stepCount = 0
    private var distanceInMeters = 0.0
    private var distanceInKm = 0.0
    private var caloriesBurned = 0.0
    private val RECOGNITION_REQUEST_CODE = 100
    private val viewmodel : HealthyViewModel by viewModels()
    private var listHealthy = mutableListOf<HealthyModel>()
    private lateinit var healthyModel: HealthyModel
    override fun setViewBinding(): ActivityExerciseBinding {
        return ActivityExerciseBinding.inflate(layoutInflater)
    }

    override fun initData() {

    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        viewmodel.getItemByDate(currentDate)
        lifecycleScope.launch {
            viewmodel.uiStateDate.collect{state ->
                when (state) {
                    is UiState.Loading ->{}
                    is UiState.Success<*> ->{
                        healthyModel = state.data as HealthyModel
                        binding.apply {
                            tvStep.text = healthyModel.stepCount.toString()
                            tvDistance.text = healthyModel.distance.toString() + getString(R.string.KM)
                            tvMoving.text = healthyModel.calories.toString() + getString(R.string.KCAL)
                        }
                    }
                    is UiState.Error -> {}
                }
            }
        }

        lifecycleScope.launch {
            viewmodel.getAll.collect{state ->
                when (state) {
                    is UiState.Loading ->{}
                    is UiState.Success<*> ->{
                        listHealthy = state.data as MutableList<HealthyModel>
                    }
                    is UiState.Error -> {}
                }
            }
        }
    }
    override fun initListener() {

        binding.apply {
            icBack.setOnClickListener {
                finish()
            }
        }

    }

}