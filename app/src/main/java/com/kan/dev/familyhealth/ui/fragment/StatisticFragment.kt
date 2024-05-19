package com.kan.dev.familyhealth.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.Entry
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.adapter.RecentAdapter
import com.kan.dev.familyhealth.base.BaseFragment
import com.kan.dev.familyhealth.data.model.BMI
import com.kan.dev.familyhealth.databinding.FragmentStatisticBinding
import com.kan.dev.familyhealth.dialog.DialogMonth
import com.kan.dev.familyhealth.interfacces.IRecentListener
import com.kan.dev.familyhealth.ui.activity.BMI.CalculatorBMIActivity
import com.kan.dev.familyhealth.utils.BMIS
import com.kan.dev.familyhealth.utils.currentMonth
import com.kan.dev.familyhealth.utils.currentYear
import com.kan.dev.familyhealth.utils.daily
import com.kan.dev.familyhealth.utils.getCurrentMonth
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick
import com.kan.dev.familyhealth.utils.monthly
import com.kan.dev.familyhealth.utils.weekly
import com.kan.dev.familyhealth.viewmodel.BMIViewModel
import com.lvt.ads.callback.InterCallback
import com.lvt.ads.util.Admob
import java.time.LocalDate


class StatisticFragment : BaseFragment<FragmentStatisticBinding>(),IRecentListener {
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentStatisticBinding {
        return FragmentStatisticBinding.inflate(layoutInflater)
    }

    private val viewModel : BMIViewModel by viewModels()
    private lateinit var intent : Intent
    private val time: String? = null
    private val minX = 0f
    private val maxX = 0f
    private val firstDotIndex = 0
    private val secondDotIndex = 0
    private val dateSelector: Float? = null
    private val result: List<Entry> = ArrayList()
    private var adapter: RecentAdapter? = null
    private val dialog: DialogMonth? = null
    private val selectorList: ArrayList<BMI> = ArrayList<BMI>()

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
    private val groupedByX: Map<Float, List<Entry>> = HashMap()
    private var x: Float? = null
    private var y:kotlin.Float? = null
    override fun initData() {
        adapter = RecentAdapter(requireActivity(),this)
        binding.tvTime.text = getCurrentMonth(requireActivity())
//        initChart()
        binding.tvUpdate.isSelected = true
        binding.Daily.isSelected = true
        binding.tvTitleStatistic.isSelected = true
        binding.Weekly.isSelected = true
        binding.Monthly.isSelected = true
        binding.tvNoData.isSelected = true
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
//        initChart()
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
        
    }

    override fun initListener() {
        
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

}