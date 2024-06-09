package com.kan.dev.familyhealth.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.RealtimeDAO
import com.kan.dev.familyhealth.data.model.BMI
import com.kan.dev.familyhealth.data.model.HealthyModel
import com.kan.dev.familyhealth.databinding.ActivityExerciseBinding
import com.kan.dev.familyhealth.dialog.DialogMonth
import com.kan.dev.familyhealth.interfacces.IMonthClickListener
import com.kan.dev.familyhealth.utils.Code
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.currentMonth
import com.kan.dev.familyhealth.utils.monthly
import com.kan.dev.familyhealth.viewmodel.HealthyViewModel
import com.kan.dev.familyhealth.viewmodel.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ExerciseActivity : BaseActivity<ActivityExerciseBinding>(), IMonthClickListener,
    SensorEventListener {

    private var currentDate: String = ""
    private lateinit var dateFormat : SimpleDateFormat
    private var stepCount = 0
    private var distanceInMeters = 0f
    private var distanceInKm = 0f
    private var caloriesBurned = 0f
    private var savedDate = ""
    private var initialStepCount: Int = 0
    private val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val sensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }
    private val accelerometerSensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }
    private val viewmodel : HealthyViewModel by viewModels()
    private var listHealthy = mutableListOf<HealthyModel>()

    private var dialog: DialogMonth? = null
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
    private lateinit var myCode : String
    private var isInformation = false
    private var code = ""
    private var listHea : ArrayList<HealthyModel> = ArrayList()
    @SuppressLint("SetTextI18n")
    override fun initData() {
        myCode = sharePre.getString(MY_CODE,"")!!
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        if (intent.hasExtra("CODE")){
            code = intent.getStringExtra("CODE")!!
            isInformation = true
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = dateFormat.format(Date())

            RealtimeDAO.getRealtimeData("$code/healthys") { snapshot ->
                for (dataSnapshot in snapshot!!.children) {
                    val model = dataSnapshot.getValue(HealthyModel::class.java)
                    model?.let {
                        listHea.add(it)
                    }
                }
                if (listHea.isNotEmpty()) {
                    listHea.forEach {
                        if (it.date == currentDate) {
                            binding.apply {
                                tvStep.text = it.steps.toString()
                                tvDistance.text = it.distance.toString() + getString(R.string.KM)
                                tvMoving.text = it.calories.toString() + getString(R.string.KCAL)
                            }
                        }
                    }
                    lineChart(listHea)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        if (!isInformation){
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            viewmodel.getItemByDate(currentDate)
            try {
                lifecycleScope.launch {
                    viewmodel.uiStateDate.collect{state ->
                        when (state) {
                            is UiState.Loading ->{}
                            is UiState.Success<*> ->{
                                if (state.data != null){
                                    healthyModel = state.data as HealthyModel
                                    binding.apply {
                                        tvStep.text = healthyModel.steps.toString()
                                        tvDistance.text = healthyModel.distance.toString() + getString(R.string.KM)
                                        tvMoving.text = healthyModel.calories.toString() + getString(R.string.KCAL)
                                    }
                                }else{
                                    val newRecord = HealthyModel(0, 0f, 0f, currentDate)
                                    val healthData = mapOf(
                                        "steps" to 0,
                                        "distance" to 0f,
                                        "calories" to 0f,
                                        "date" to currentDate
                                    )
                                    RealtimeDAO.pushRealtimeData("$myCode/healthys/$currentDate",healthData){
                                        viewmodel.insert(newRecord)
                                    }
                                }
                            }
                            is UiState.Error -> {}
                        }
                    }
                }

                RealtimeDAO.getRealtimeData("$myCode/healthys") { snapshot ->
                    for (dataSnapshot in snapshot!!.children) {
                        val model = dataSnapshot.getValue(HealthyModel::class.java)
                        model?.let {
                            listHea.add(it)
                        }
                    }
                    if (listHea.isNotEmpty()) {
                        lineChart(listHea)
                    }
                }
            }catch (e : Exception){
                e.printStackTrace()
            }

        }
    }
    override fun initListener() {

        binding.apply {
            icBack.setOnClickListener {
                finish()
            }
            Time.setOnClickListener {
                dialog = DialogMonth(this@ExerciseActivity, this@ExerciseActivity)
                dialog!!.show()
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
                val firstDotIndex = time.indexOf("-")
                val secondDotIndex = time.indexOf("-", firstDotIndex + 1)
                val months = time.substring(firstDotIndex + 1, secondDotIndex).toInt()
                Log.d("Kan", "Month :$months")
                if (months == currentMonth) {
                    x = time.substring(time.lastIndexOf('-') + 1).toFloat()
                    y = it.steps.toFloat()
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
                    position = XAxis.XAxisPosition.BOTTOM
                }
                moveViewToX(0f)
                xAxis.apply {
                    setDrawGridLines(false)
                    textColor = ContextCompat.getColor(this@ExerciseActivity, R.color.white)
                    axisLineColor = ContextCompat.getColor(this@ExerciseActivity, R.color.transfer)
                    axisMinimum = minX
                    axisMaximum = maxX
                    granularity = 1f
                }
                yAxis.apply {
                    textColor = ContextCompat.getColor(this@ExerciseActivity, R.color.white)
                    axisMinimum = 0f
                    axisMaximum = 10000f
                    axisLineColor = ContextCompat.getColor(this@ExerciseActivity, R.color.transfer)
                }
                setVisibleXRangeMinimum(5f)
                setVisibleXRangeMaximum(7f)
                setVisibleYRangeMaximum(7000f, YAxis.AxisDependency.LEFT)
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

    override fun onClickListener(month: String?, index: Int) {
        currentMonth = index
    }

    override fun onClickYearListener(year: Int) {

    }

    override fun onHideSystemBar() {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            currentDate = dateFormat.format(Date())
            if (savedDate != currentDate) {
                initialStepCount = event.values[0].toInt()
                saveCurrentState(initialStepCount, currentDate)
            }
            initialStepCount = sharePre.getInt("PREF_INITIAL_STEP_COUNT", 0)
            stepCount = event.values[0].toInt()
            distanceInMeters = (stepCount * 0.762).toFloat()
            distanceInKm = distanceInMeters / 1000
            caloriesBurned = (stepCount * 0.05).toFloat()
            distanceInKm = Math.round(distanceInKm*100)/100.toFloat()
            caloriesBurned = Math.round(caloriesBurned*100)/100.toFloat()

            viewmodel.getItemByDate(currentDate)
            lifecycleScope.launch {
                viewmodel.uiStateDate.collect{state ->
                    when (state) {
                        is UiState.Loading ->{}
                        is UiState.Success<*> ->{
                            if (state.data != null){
                                healthyModel = state.data as HealthyModel
                                healthyModel.steps = stepCount
                                healthyModel.distance = distanceInKm
                                healthyModel.calories = caloriesBurned
                                val healthData = mapOf(
                                    "steps" to stepCount,
                                    "distance" to distanceInKm,
                                    "calories" to caloriesBurned,
                                )
                                RealtimeDAO.updateRealtimeData("$myCode/healthys/$currentDate",healthData){
                                   viewmodel.update(healthyModel)
                                }
                            }
                        }
                        is UiState.Error -> {}
                    }
                }
            }

        }
//        else if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
//            handleAcceleration(event.values)
//        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
    private fun handleAcceleration(values: FloatArray) {
        val acceleration = values.map { it * it }.sum().toDouble().let { Math.sqrt(it) }
        val threshold = 10.0
        if (acceleration > threshold) {
            dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            currentDate = dateFormat.format(Date())
            if (savedDate != currentDate) {
                stepCount = 0
            }
            stepCount += 1
            distanceInMeters = (stepCount * 0.762).toFloat()
            distanceInKm = distanceInMeters / 1000
            caloriesBurned = (stepCount * 0.05).toFloat()
            distanceInKm = Math.round(distanceInKm*100)/100.toFloat()
            caloriesBurned = Math.round(caloriesBurned*100)/100.toFloat()

            viewmodel.getItemByDate(currentDate)
            lifecycleScope.launch {
                viewmodel.uiStateDate.collect{state ->
                    when (state) {
                        is UiState.Loading ->{}
                        is UiState.Success<*> ->{
                            if (state.data != null){
                                healthyModel = state.data as HealthyModel
                                healthyModel.steps = stepCount
                                healthyModel.distance = distanceInKm
                                healthyModel.calories = caloriesBurned
                                val healthData = mapOf(
                                    "steps" to stepCount,
                                    "distance" to distanceInKm,
                                    "calories" to caloriesBurned,
                                )
                                RealtimeDAO.updateRealtimeData("$Code/healthys/$currentDate",healthData){
                                    viewmodel.update(healthyModel)
                                }
                            }
                        }
                        is UiState.Error -> {}
                    }
                }
            }
        }
    }

    private fun saveCurrentState(initialStepCount: Int, currentDate: String) {
        sharePre.putInt("PREF_INITIAL_STEP_COUNT", initialStepCount)
        sharePre.putString("PREF_DATE", currentDate)
    }

}