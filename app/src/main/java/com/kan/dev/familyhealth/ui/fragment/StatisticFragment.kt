package com.kan.dev.familyhealth.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.adapter.RecentAdapter
import com.kan.dev.familyhealth.base.BaseFragment
import com.kan.dev.familyhealth.data.model.BMI
import com.kan.dev.familyhealth.databinding.FragmentStatisticBinding
import com.kan.dev.familyhealth.dialog.DialogMonth
import com.kan.dev.familyhealth.interfacces.IMonthClickListener
import com.kan.dev.familyhealth.interfacces.IRecentListener
import com.kan.dev.familyhealth.ui.activity.BMI.BMIActivity
import com.kan.dev.familyhealth.ui.activity.BMI.CalculatorBMIActivity
import com.kan.dev.familyhealth.utils.BMIS
import com.kan.dev.familyhealth.utils.currentMonth
import com.kan.dev.familyhealth.utils.currentYear
import com.kan.dev.familyhealth.utils.daily
import com.kan.dev.familyhealth.utils.getCurrentMonth
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick
import com.kan.dev.familyhealth.utils.monthly
import com.kan.dev.familyhealth.utils.months
import com.kan.dev.familyhealth.utils.weekly
import com.kan.dev.familyhealth.utils.years
import com.kan.dev.familyhealth.viewmodel.BMIViewModel
import com.lvt.ads.callback.InterCallback
import com.lvt.ads.util.Admob
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class StatisticFragment : BaseFragment<FragmentStatisticBinding>(),IRecentListener,IMonthClickListener {
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentStatisticBinding {
        return FragmentStatisticBinding.inflate(layoutInflater)
    }

    private val viewModel : BMIViewModel by viewModels()
    private lateinit var intent : Intent
    private var time: String? = null
    private var minX = 0f
    private var maxX = 0f
    private var firstDotIndex = 0
    private var secondDotIndex = 0
    private var dateSelector: Float? = null
    private val result: MutableList<Entry> = ArrayList()
    private var adapter: RecentAdapter? = null
    private var dialog: DialogMonth? = null
    private val selectorList: ArrayList<BMI> = ArrayList()

    private val weekly_1 = ArrayList<Float>()
    private val weekly_2 = ArrayList<Float>()
    private val weekly_3 = ArrayList<Float>()
    private val weekly_4 = ArrayList<Float>()

    private val January = ArrayList<Float>()
    private val February = ArrayList<Float>()
    private val March = ArrayList<Float>()
    private val April = ArrayList<Float>()
    private val May = ArrayList<Float>()
    private val June = ArrayList<Float>()
    private val July = ArrayList<Float>()
    private val August = ArrayList<Float>()
    private val September = ArrayList<Float>()
    private val October = ArrayList<Float>()
    private val November = ArrayList<Float>()
    private val December = ArrayList<Float>()
    private val groupedByX: MutableMap<Float, ArrayList<Entry>> = HashMap()
    private var x: Float? = null
    private var y: Float? = null
    override fun initData() {
        adapter = RecentAdapter(requireActivity(),this)
    }

    @SuppressLint("NewApi")
    override fun onStart() {
        super.onStart()
        Log.d("KanMobile", "onStart")
        daily = true
        weekly = false
        monthly = false
        binding.apply {
            Daily.setBackgroundResource(R.drawable.bg_button_magenta)
            Weekly.setBackgroundResource(R.color.transfer)
            Monthly.setBackgroundResource(R.color.transfer)
        }
        initChart()
    }
    override fun onStop() {
        super.onStop()
        adapter?.notifyDataSetChanged()
        binding.lineChartBmi.highlightValue(null)
        Log.d("KanMobile", "onStop")
    }

    @SuppressLint("NewApi")
    override fun onDestroy() {
        currentYear = LocalDate.now().year
        currentMonth = LocalDate.now().monthValue
        super.onDestroy()
    }
    override fun initView() {
        binding.tvTime.text = getCurrentMonth(requireActivity())
        initChart()
        binding.tvUpdate.isSelected = true
        binding.Daily.isSelected = true
        binding.tvTitleStatistic.isSelected = true
        binding.Weekly.isSelected = true
        binding.Monthly.isSelected = true
        binding.tvNoData.isSelected = true
    }

    override fun initListener() {
        binding.apply {
            Time.setOnClickListener {
                dialog = DialogMonth(requireContext(), this@StatisticFragment)
                dialog!!.show()
            }
            Daily.setOnClickListener {
                daily = true
                weekly = false
                monthly = false
                Daily.setBackgroundResource(R.drawable.bg_button_magenta)
                Weekly.setBackgroundResource(R.color.transfer)
                Monthly.setBackgroundResource(R.color.transfer)
                initChart()
            }

            Weekly.setOnClickListener {
                daily = false
                weekly = true
                monthly = false
                Daily.setBackgroundResource(R.color.transfer)
                Weekly.setBackgroundResource(R.drawable.bg_button_magenta)
                Monthly.setBackgroundResource(R.color.transfer)
                initChart()
            }
            Monthly.setOnClickListener {
                daily = false
                weekly = false
                monthly = true
                Daily.setBackgroundResource(R.color.transfer)
                Weekly.setBackgroundResource(R.color.transfer)
                Monthly.setBackgroundResource(R.drawable.bg_button_magenta)
                initChart()
            }
            tvUpdate.setOnClickListener {
                if (isClick) {
                    isClick = false
                    startActivity(Intent(activity, BMIActivity::class.java))
                    if (activity != null) {
                        finishAffinity(requireActivity())
                    }
                    Handler(Looper.getMainLooper()).postDelayed({ isClick = true }, 500)
                }
            }

            lineChartBmi.setOnChartValueSelectedListener(object :
                OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry, h: Highlight) {
                    dateSelector = e.x
                    viewModel.getAll.observe(requireActivity()) { bmis ->
                            selectorList.clear()
                            for (it in bmis!!) {
                                time = it.time
                                firstDotIndex = time!!.indexOf(".")
                                secondDotIndex = time!!.indexOf(".", firstDotIndex + 1)
                                months =
                                    time!!.substring(firstDotIndex + 1, secondDotIndex).toInt()
                                years = time!!.substring(0, 4).toInt()
                                x = java.lang.Float.valueOf(
                                    time!!.substring(
                                        time!!.lastIndexOf('.') + 1
                                    )
                                )
                                if (monthly) {
                                    if (dateSelector == 1f) {
                                        if (months == 1) {
                                            selectorList.add(it)
                                        }
                                    } else if (dateSelector == 2f) {
                                        if (months == 2) {
                                            selectorList.add(it)
                                        }
                                    } else if (dateSelector == 3f) {
                                        if (months == 3) {
                                            selectorList.add(it)
                                        }
                                    } else if (dateSelector == 4f) {
                                        if (months == 4) {
                                            selectorList.add(it)
                                        }
                                    } else if (dateSelector == 5f) {
                                        if (months == 5) {
                                            selectorList.add(it)
                                        }
                                    } else if (dateSelector == 6f) {
                                        if (months == 6) {
                                            selectorList.add(it)
                                        }
                                    } else if (dateSelector == 7f) {
                                        if (months == 7) {
                                            selectorList.add(it)
                                        }
                                    } else if (dateSelector == 8f) {
                                        if (months == 8) {
                                            selectorList.add(it)
                                        }
                                    } else if (dateSelector == 9f) {
                                        if (months == 9) {
                                            selectorList.add(it)
                                        }
                                    } else if (dateSelector == 10f) {
                                        if (months == 10) {
                                            selectorList.add(it)
                                        }
                                    } else if (dateSelector == 11f) {
                                        if (months == 11) {
                                            selectorList.add(it)
                                        }
                                    } else if (dateSelector == 12f) {
                                        if (months == 12) {
                                            selectorList.add(it)
                                        }
                                    }
                                } else {
                                    if (months == currentMonth) {
                                        if (daily) {
                                            if (dateSelector == x) {
                                                selectorList.add(it)
                                            }
                                        } else if (weekly) {
                                            if (dateSelector == 1f) {
                                                if (x!! < 8) {
                                                    selectorList.add(it)
                                                }
                                            } else if (dateSelector == 2f) {
                                                if (x!! >= 8 && x!! < 16) {
                                                    selectorList.add(it)
                                                }
                                            } else if (dateSelector == 3f) {
                                                if (x!! >= 16 && x!! < 23) {
                                                    selectorList.add(it)
                                                }
                                            } else if (dateSelector == 4f) {
                                                if (x!! >= 24) {
                                                    selectorList.add(it)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            adapter!!.setItems(selectorList)
                            rcvBmi.adapter = adapter
                            rcvBmi.layoutManager =
                                LinearLayoutManager(
                                    requireActivity(),
                                    RecyclerView.VERTICAL,
                                    false
                                )
                        }
                }
                override fun onNothingSelected() {}
            })

        }
    }
    private fun initChart() {
        selectorList.clear()
        adapter!!.setItems(selectorList)
        if (daily) {
            binding.tvTime.text = getCurrentMonth(requireActivity())
            initChartDaily()
        } else if (weekly) {
            binding.tvTime.text = getCurrentMonth(requireActivity())
            initChartWeekly()
        } else if (monthly) {
            binding.tvTime.text = currentYear.toString()
            initChartMonthly()
        }
    }
    private fun initChartDaily() {
        x = 0f
        y = 0f
        val xAxis: XAxis = binding.lineChartBmi.xAxis
        val yAxis: YAxis = binding.lineChartBmi.axisLeft
        val values = mutableListOf<Entry>()
        viewModel.getAll.observe(requireActivity(), Observer { bmis ->
            if (bmis.isNotEmpty()) {
                for (it in bmis) {
                    val time = it.time
                    val firstDotIndex = time.indexOf(".")
                    val secondDotIndex = time.indexOf(".", firstDotIndex + 1)
                    val months = time.substring(firstDotIndex + 1, secondDotIndex).toInt()
                    Log.d("Kan", "Month :$months")
                    if (months == currentMonth) {
                        x = time.substring(time.lastIndexOf('.') + 1).toFloat()
                        y = it.bmi
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
                    color = ContextCompat.getColor(requireContext(), R.color.white)
                    valueTextColor = ContextCompat.getColor(requireContext(), R.color.white)
                }

                val dataSets = mutableListOf<ILineDataSet>().apply {
                    add(dataSet)
                }

                val lineData = LineData(dataSets)
                binding.lineChartBmi.data = lineData

                with(binding.lineChartBmi) {
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
                        textColor = ContextCompat.getColor(requireContext(), R.color.white)
                        axisLineColor = ContextCompat.getColor(requireContext(), R.color.transfer)
                        axisMinimum = minX
                        axisMaximum = maxX
                        granularity = 1f
                    }
                    yAxis.apply {
                        textColor = ContextCompat.getColor(requireContext(), R.color.white)
                        axisMinimum = 0f
                        axisMaximum = 80f
                        axisLineColor = ContextCompat.getColor(requireContext(), R.color.transfer)
                    }
                    setVisibleXRangeMinimum(5f)
                    setVisibleXRangeMaximum(7f)
                    setVisibleYRangeMaximum(70f, YAxis.AxisDependency.LEFT)
                    invalidate()
                }

                if (values.isNotEmpty()) {
                    binding.lineChartBmi.visibility = View.VISIBLE
                    binding.tvNoData.visibility = View.INVISIBLE
                } else {
                    binding.lineChartBmi.visibility = View.INVISIBLE
                    binding.tvNoData.visibility = View.VISIBLE
                }
            } else {
                if (values.isNotEmpty()) {
                    binding.lineChartBmi.visibility = View.VISIBLE
                    binding.tvNoData.visibility = View.INVISIBLE
                } else {
                    binding.lineChartBmi.visibility = View.INVISIBLE
                    binding.tvNoData.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun initChartWeekly() {
        x = 0f
        y = 0f
        val xAxis = binding.lineChartBmi.xAxis
        val yAxis = binding.lineChartBmi.axisLeft
        weekly_1.clear()
        weekly_2.clear()
        weekly_3.clear()
        weekly_4.clear()
        val values = mutableListOf<Entry>()

        viewModel.getAll.observe(requireActivity(), Observer { bmis ->
            if (bmis.isNotEmpty()) {
                for (it in bmis) {
                    time = it.time
                    firstDotIndex = time!!.indexOf(".")
                    secondDotIndex = time!!.indexOf(".", firstDotIndex + 1)
                    val months = time!!.substring(firstDotIndex + 1, secondDotIndex).toInt()
                    Log.d("Kan", "Month: $months")
                    if (months == currentMonth) {
                        x = time!!.substring(time!!.lastIndexOf('.') + 1).toFloat()
                        y = it.bmi.toFloat()
                        values.add(Entry(x!!, y!!))

                        groupedByX.clear()
                        for (entry in values) {
                            groupedByX[entry.x]?.let {
                                it.add(entry)
                            } ?: run {
                                groupedByX[entry.x] = mutableListOf(entry) as ArrayList<Entry>
                            }
                        }

                        result.clear()
                        for ((x, entriesWithSameX) in groupedByX) {
                            val averageY = entriesWithSameX.map { it.y }.average()
                            val roundedAverageY = String.format("%.1f", averageY).replace(",", ".").toFloat()
                            Log.d("kn", roundedAverageY.toString())
                            result.add(Entry(x, roundedAverageY))
                        }

                        for (entryValue in result) {
                            Log.d("ka", entryValue.x.toString())
                            when {
                                entryValue.x >= 1f && entryValue.x < 8f -> weekly_1.add(entryValue.y)
                                entryValue.x >= 8f && entryValue.x < 16f -> weekly_2.add(entryValue.y)
                                entryValue.x >= 16f && entryValue.x < 23f -> weekly_3.add(entryValue.y)
                                entryValue.x >= 23f -> weekly_4.add(entryValue.y)
                            }

                        }

                        values.clear()
                        if (weekly_1.isNotEmpty()) {
                            val averageWeekly1 = weekly_1.average().toFloat()
                            values.add(Entry(1f, averageWeekly1))
                        }

                        if (weekly_2.isNotEmpty()) {
                            val averageWeekly2 = weekly_2.average().toFloat()
                            values.add(Entry(2f, averageWeekly2))
                        }

                        if (weekly_3.isNotEmpty()) {
                            val averageWeekly3 = weekly_3.average().toFloat()
                            values.add(Entry(3f, averageWeekly3))
                        }

                        if (weekly_4.isNotEmpty()) {
                            val averageWeekly4 = weekly_4.average().toFloat()
                            values.add(Entry(4f, averageWeekly4))
                        }
                    }
                }

                values.sortBy { it.x }
                if (values.isNotEmpty()) {
                    minX = values.first().x
                    if (minX > 2) minX -= 2
                    maxX = values.last().x
                    if (maxX < 28) maxX += 2
                }

                val dataSet = LineDataSet(values, "Label").apply {
                    setDrawValues(true)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    cubicIntensity = 0.2f
                    color = ContextCompat.getColor(requireContext(), R.color.white)
                    valueTextColor = ContextCompat.getColor(requireContext(), R.color.white)
                }

                val dataSets = listOf<ILineDataSet>(dataSet)
                val lineData = LineData(dataSets)
                binding.lineChartBmi.data = lineData

                binding.lineChartBmi.apply {
                    description.isEnabled = false
                    legend.isEnabled = false
                    setTouchEnabled(true)
                    isDragEnabled = true
                    setScaleEnabled(true)
                    setPinchZoom(false)
                    axisRight.isEnabled = false
                    axisRight.setDrawLabels(false)
                    xAxis.isEnabled = true
                    xAxis.setDrawLabels(true)
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    moveViewToX(0f)
                    setVisibleXRangeMinimum(5f)
                    setVisibleXRangeMaximum(7f)
                    setVisibleYRangeMaximum(70f, YAxis.AxisDependency.LEFT)
                    invalidate()
                }

                xAxis.apply {
                    setDrawGridLines(false)
                    textColor = ContextCompat.getColor(requireContext(), R.color.white)
                    axisLineColor = ContextCompat.getColor(requireContext(), R.color.transfer)
                    axisMinimum = 0f
                    axisMaximum = 4f
                    granularity = 1f
                }

                yAxis.apply {
                    textColor = ContextCompat.getColor(requireContext(), R.color.white)
                    axisMinimum = 0f
                    axisMaximum = 80f
                    axisLineColor = ContextCompat.getColor(requireContext(), R.color.transfer)
                }

                if (values.isNotEmpty()) {
                    binding.lineChartBmi.visibility = View.VISIBLE
                    binding.tvNoData.visibility = View.INVISIBLE
                } else {
                    binding.lineChartBmi.visibility = View.INVISIBLE
                    binding.tvNoData.visibility = View.VISIBLE
                }
            } else {
                if (values.isNotEmpty()) {
                    binding.lineChartBmi.visibility = View.VISIBLE
                    binding.tvNoData.visibility = View.INVISIBLE
                } else {
                    binding.lineChartBmi.visibility = View.INVISIBLE
                    binding.tvNoData.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun initChartMonthly() {
        January.clear()
        February.clear()
        March.clear()
        April.clear()
        May.clear()
        June.clear()
        July.clear()
        August.clear()
        September.clear()
        October.clear()
        November.clear()
        December.clear()

        x = 0f
        y = 0f

        val xAxis = binding.lineChartBmi.xAxis
        val yAxis = binding.lineChartBmi.axisLeft
        val values = mutableListOf<Entry>()

        viewModel.getAll.observe(requireActivity(), Observer { bmis ->
            if (bmis.isNotEmpty()) {
                for (it in bmis) {
                    time = it.time
                    firstDotIndex = time!!.indexOf(".")
                    secondDotIndex = time!!.indexOf(".", firstDotIndex + 1)
                    years = time!!.substring(0, 4).toInt()
                    months = time!!.substring(firstDotIndex + 1, secondDotIndex).toInt()

                    if (currentYear == years) {
                        when (months) {
                            1 -> January.add(it.bmi.toFloat())
                            2 -> February.add(it.bmi.toFloat())
                            3 -> March.add(it.bmi.toFloat())
                            4 -> April.add(it.bmi.toFloat())
                            5 -> May.add(it.bmi.toFloat())
                            6 -> June.add(it.bmi.toFloat())
                            7 -> July.add(it.bmi.toFloat())
                            8 -> August.add(it.bmi.toFloat())
                            9 -> September.add(it.bmi.toFloat())
                            10 -> October.add(it.bmi.toFloat())
                            11 -> November.add(it.bmi.toFloat())
                            12 -> December.add(it.bmi.toFloat())
                        }
                    }
                }

                values.clear()
                if (January.isNotEmpty()) values.add(Entry(1f, average(January)))
                if (February.isNotEmpty()) values.add(Entry(2f, average(February)))
                if (March.isNotEmpty()) values.add(Entry(3f, average(March)))
                if (April.isNotEmpty()) values.add(Entry(4f, average(April)))
                if (May.isNotEmpty()) values.add(Entry(5f, average(May)))
                if (June.isNotEmpty()) values.add(Entry(6f, average(June)))
                if (July.isNotEmpty()) values.add(Entry(7f, average(July)))
                if (August.isNotEmpty()) values.add(Entry(8f, average(August)))
                if (September.isNotEmpty()) values.add(Entry(9f, average(September)))
                if (October.isNotEmpty()) values.add(Entry(10f, average(October)))
                if (November.isNotEmpty()) values.add(Entry(11f, average(November)))
                if (December.isNotEmpty()) values.add(Entry(12f, average(December)))

                values.sortBy { it.x }

                Log.d("KanMobile", "Size: ${values.size}")

                val dataSet = LineDataSet(values, "Label").apply {
                    setDrawValues(true)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    cubicIntensity = 0.2f
                    color = ContextCompat.getColor(requireContext(), R.color.white)
                    valueTextColor = ContextCompat.getColor(requireContext(), R.color.white)
                }

                val dataSets = mutableListOf<ILineDataSet>(dataSet)
                val lineData = LineData(dataSets)
                binding.lineChartBmi.data = lineData

                binding.lineChartBmi.apply {
                    description.isEnabled = false
                    legend.isEnabled = false
                    setTouchEnabled(true)
                    isDragEnabled = true
                    setScaleEnabled(true)
                    setPinchZoom(false)
                    axisRight.isEnabled = false
                    axisRight.setDrawLabels(false)
                    xAxis.isEnabled = true
                    xAxis.setDrawLabels(true)
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    moveViewToX(0f)
                    setVisibleXRangeMinimum(5f)
                    setVisibleXRangeMaximum(7f)
                    setVisibleYRangeMaximum(70f, YAxis.AxisDependency.LEFT)
                    invalidate()
                }

                xAxis.apply {
                    setDrawGridLines(false)
                    textColor = ContextCompat.getColor(requireContext(), R.color.white)
                    axisLineColor = ContextCompat.getColor(requireContext(), R.color.transfer)
                    axisMinimum = 0f
                    axisMaximum = 12.5f
                    granularity = 1f
                }

                yAxis.apply {
                    textColor = ContextCompat.getColor(requireContext(), R.color.white)
                    axisMinimum = 0f
                    axisMaximum = 80f
                    axisLineColor = ContextCompat.getColor(requireContext(), R.color.transfer)
                }

                if (values.isNotEmpty()) {
                    binding.lineChartBmi.visibility = View.VISIBLE
                    binding.tvNoData.visibility = View.INVISIBLE
                } else {
                    binding.lineChartBmi.visibility = View.INVISIBLE
                    binding.tvNoData.visibility = View.VISIBLE
                }
            } else {
                if (values.isNotEmpty()) {
                    binding.lineChartBmi.visibility = View.VISIBLE
                    binding.tvNoData.visibility = View.INVISIBLE
                } else {
                    binding.lineChartBmi.visibility = View.INVISIBLE
                    binding.tvNoData.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun average(list: ArrayList<Float>): Float {
        return list.sum() / list.size
    }


    override fun deleteRecent(bmi: BMI) {
        viewModel.delete(bmi)
    }
    override fun clickRecent(item: BMI) {
        if (isClick) {
            isClick = false
            Admob.getInstance().showInterAll(requireContext(), object : InterCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    item.isRecent = true
                    intent = Intent(requireContext(), CalculatorBMIActivity::class.java)
                    intent.putExtra(BMIS, item)
                    startActivity(intent)
                }
            })
            handler.postDelayed(Runnable { com.kan.dev.familyhealth.utils.isClick = true }, 500)
        }
    }

    override fun onClickListener(month: String?, index: Int) {
        if (monthly) {
            binding.tvTime.text = month
            currentMonth = index
            initChart()
        }
    }

    override fun onClickYearListener(year: Int) {
        if (monthly) {
            binding.tvTime.text = year.toString()
            currentYear = year
            initChart()
        }
    }

    override fun onHideSystemBar() {
        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        } else {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }
}