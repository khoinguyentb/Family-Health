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
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.model.HealthyModel
import com.kan.dev.familyhealth.databinding.ActivityExerciseBinding
import com.kan.dev.familyhealth.utils.currentMonth
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

    private val result: MutableList<Entry> = ArrayList()
    private val groupedByX: MutableMap<Float, ArrayList<Entry>> = HashMap()
    private var x: Float? = null
    private var y: Float? = null
    private var minX = 0f
    private var maxX = 0f
    private lateinit var healthyModel: HealthyModel
    override fun setViewBinding(): ActivityExerciseBinding {
        return ActivityExerciseBinding.inflate(layoutInflater)
    }

    override fun initData() {

    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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
                        lineChart(listHealthy)
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

    private fun lineChart(list: List<HealthyModel>){
        x = 0f
        y = 0f
        val xAxis: XAxis = binding.lineChart.xAxis
        val yAxis: YAxis = binding.lineChart.axisLeft
        val values = mutableListOf<Entry>()

        if (list.isNotEmpty()) {
            for (it in list) {
                val time = it.date
                val firstDotIndex = time.indexOf("/")
                val secondDotIndex = time.indexOf("/", firstDotIndex + 1)
                val months = time.substring(firstDotIndex + 1, secondDotIndex).toInt()
                Log.d("Kan", "Month :$months")
                if (months == currentMonth) {
                    x = time.substring(time.lastIndexOf('/') + 1).toFloat()
                    y = it.stepCount.toFloat()
                    values.add(Entry(x!!, y!!))

                    groupedByX.clear()
                    for (entry in values) {
                        groupedByX[entry.x]?.add(entry) ?: run {
                            val entriesWithSameX = mutableListOf(entry)
                            groupedByX[entry.x] = entriesWithSameX as ArrayList<Entry>
                        }
                    }

                    result.clear()
                    for ((key, entriesWithSameX) in groupedByX) {
                        val averageY = entriesWithSameX.map { it.y.toDouble() }.average().let {
                            String.format("%.1f", it).replace(",", ".").toDouble()
                        }
                        Log.d("kn", averageY.toString())
                        result.add(Entry(key, averageY.toFloat()))
                    }
                    values.clear()
                    values.addAll(result)
                }
            }

            values.sortBy { it.x }
            if (values.isNotEmpty()) {
                minX = values.first().x
                if (minX > 2) minX -= 2 else minX = 0f
                maxX = values.last().x
                if (maxX < 28) maxX += 2
            }

            val dataSet = LineDataSet(values, "Label").apply {
                setDrawValues(true)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                cubicIntensity = 0.2f
                color = ContextCompat.getColor(this@ExerciseActivity, R.color.white)
                valueTextColor = ContextCompat.getColor(this@ExerciseActivity, R.color.white)
            }

            val dataSets = mutableListOf<ILineDataSet>().apply {
                add(dataSet)
            }

            val lineData = LineData(dataSets)
            binding.lineChart.data = lineData

            with(binding.lineChart) {
                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(false)
                axisRight.isEnabled = false
                axisRight.setDrawLabels(false)
                xAxis.apply {
                    isEnabled = true
                    setDrawLabels(true)
                    position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                }
                moveViewToX(0f)
                xAxis.apply {
                    setDrawGridLines(false)
                    textColor = androidx.core.content.ContextCompat.getColor(this@ExerciseActivity, R.color.white)
                    axisLineColor = androidx.core.content.ContextCompat.getColor(this@ExerciseActivity, R.color.transfer)
                    axisMinimum = minX
                    axisMaximum = maxX
                    granularity = 1f
                }
                yAxis.apply {
                    textColor = androidx.core.content.ContextCompat.getColor(this@ExerciseActivity, R.color.white)
                    axisMinimum = 0f
                    axisMaximum = 10000f
                    axisLineColor = androidx.core.content.ContextCompat.getColor(this@ExerciseActivity, R.color.transfer)
                }
                setVisibleXRangeMinimum(500f)
                setVisibleXRangeMaximum(700f)
                setVisibleYRangeMaximum(7000f, com.github.mikephil.charting.components.YAxis.AxisDependency.LEFT)
                invalidate()
            }

            if (values.isNotEmpty()) {
                binding.lineChart.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.INVISIBLE
            } else {
                binding.lineChart.visibility = View.INVISIBLE
                binding.tvNoData.visibility = View.VISIBLE
            }
        } else {
            if (values.isNotEmpty()) {
                binding.lineChart.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.INVISIBLE
            } else {
                binding.lineChart.visibility = View.INVISIBLE
                binding.tvNoData.visibility = View.VISIBLE
            }
        }

    }

}