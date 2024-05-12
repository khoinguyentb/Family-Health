package com.kan.dev.familyhealth.ui.fragment

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseFragment
import com.kan.dev.familyhealth.data.model.BMI
import com.kan.dev.familyhealth.databinding.FragmentCalculatorBinding
import com.kan.dev.familyhealth.dialog.DialogDate
import com.kan.dev.familyhealth.interfacces.IDateClickListener
import com.kan.dev.familyhealth.ui.activity.CalculatorBMIActivity
import com.kan.dev.familyhealth.ui.activity.RecentActivity
import com.kan.dev.familyhealth.utils.BMIS
import com.kan.dev.familyhealth.utils.DATE_CHANGE
import com.kan.dev.familyhealth.utils.FEMALE
import com.kan.dev.familyhealth.utils.MALE
import com.kan.dev.familyhealth.utils.SharePreferencesUtils
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.pow

@AndroidEntryPoint
class CalculatorFragment : BaseFragment<FragmentCalculatorBinding>(),IDateClickListener {
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCalculatorBinding {
        return FragmentCalculatorBinding.inflate(layoutInflater)
    }
    private lateinit var date: String
    private lateinit var genders: String
    private var age: Int = 0
    private var weight: Float = 0F
    private var height: Float = 0F
    private var bmi: Float = 0f
    private var checkCm: Boolean = false
    private var checkSt: Boolean = false
    private var checkKg: Boolean = false
    private var checkLb: Boolean = false
    private lateinit var intent: Intent
    private lateinit var mBmi: BMI

    private var isClick: Boolean = true
    private lateinit var handler: Handler
    private lateinit var dialogDate: DialogDate

    @Inject
    lateinit var sharePre: SharePreferencesUtils

    private fun setupNumberPicker() {
        binding.numberPicker.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.poppins_bold)
        binding.numberPicker.wrapSelectorWheel = true
        binding.numberPicker.isScrollerEnabled = true
        binding.numberPicker.setSelectedTypeface(
            ResourcesCompat.getFont(
                requireContext(),
                R.font.poppins_bold
            )
        )
    }

    override fun onStart() {
        super.onStart()
        date = getCurrentDateFormatted()
        binding.tvTime.text = date
    }

    private val maleClickListener = View.OnClickListener { v ->
        genders = MALE
        binding.Male.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.selector_sex))
        binding.Female.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.unselector_sex))
    }

    private val timeClickListener = View.OnClickListener { v ->
        if (isClick) {
            isClick = true
            dialogDate.show()
            handler.postDelayed({ isClick = true }, 500)
        }
    }

    private val femaleClickListener = View.OnClickListener { v ->
        genders = FEMALE
        binding.Female.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.selector_sex))
        binding.Male.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.unselector_sex))
    }

    private val recentClickListener = View.OnClickListener { v ->
        if (isClick) {
            isClick = false
            intent = Intent(requireContext(), RecentActivity::class.java)
            startActivity(intent)
            handler.postDelayed({ isClick = true }, 500)
        }
    }

    private val reloadClickListener = View.OnClickListener { v ->
        if (isClick) {
            isClick = false
            date = getCurrentDateFormatted()
            binding.tvTime.text = date
            genders = MALE
            binding.Male.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.selector_sex)
            binding.Female.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.unselector_sex)
            checkCm = true
            checkKg = false
            checkLb = false
            checkSt = true
            binding.rulerHeight.initViewParam(50f, 30f, 300f, 0.1f)
            binding.rulerWeight.initViewParam(20f, 0f, 48f, 0.1f)
            binding.rulerHeight.setUnit(getString(R.string.cm))
            binding.rulerWeight.setUnit(getString(R.string.st))
            binding.numberPicker.value = 20
            handler.postDelayed({ isClick = true }, 500)
        }
    }

    private val calculatorClickListener = View.OnClickListener { v ->
        if (isClick) {
            isClick = false
            age = binding.numberPicker.value

            weight = binding.rulerWeight.value
            height = binding.rulerHeight.value

            if (!checkCm) {
                height *= 30.48f
            }
            if (checkKg) {
                weight = weight
            } else if (checkLb) {
                weight *= 0.45359237f
            } else if (checkSt) {
                weight *= 6.350293f
            }
            bmi = weight / (height.toDouble() / 100).pow(2.0).toFloat()

            bmi = (Math.round(bmi * 100) / 100).toString().toFloat()

            if (bmi > 70) {
                Toast.makeText(requireContext(), getString(R.string.bmi_error), Toast.LENGTH_SHORT)
                    .show()
            } else {
                mBmi = BMI(
                    date,
                    genders,
                    age,
                    binding.rulerWeight.value,
                    checkCm,
                    checkSt,
                    checkKg,
                    checkLb,
                    binding.rulerHeight.value,
                    bmi
                    )
                sharePre.putBoolean(DATE_CHANGE, true)
                intent = Intent(requireContext(), CalculatorBMIActivity::class.java)
                intent.putExtra(BMIS, mBmi)
                startActivity(intent)
            }
            handler.postDelayed({ isClick = true }, 500)
        }
    }

    private val cmClickListener = View.OnClickListener { v ->
        if (isClick) {
            isClick = true
            checkCm = true
            binding.CM.setBackgroundResource(R.drawable.bg_unit)
            binding.FT.setBackgroundResource(R.color.transfer)
            binding.rulerHeight.initViewParam(50f, 30f, 300f, 0.1f)
            binding.rulerHeight.setUnit(getString(R.string.cm))
            handler.postDelayed({ isClick = true }, 500)
        }
    }

    private val ftClickListener = View.OnClickListener { v ->
        if (isClick) {
            isClick = true
            checkCm = false
            binding.FT.setBackgroundResource(R.drawable.bg_unit)
            binding.CM.setBackgroundResource(R.color.transfer)
            binding.rulerHeight.initViewParam(5f, 0f, 10f, 0.1f)
            binding.rulerHeight.setUnit(getString(R.string.ft))
            handler.postDelayed({ isClick = true }, 500)
        }
    }

    private val stClickListener = View.OnClickListener { v ->
        if (isClick) {
            isClick = true
            checkSt = true
            checkLb = false
            checkKg = false
            binding.ST.setBackgroundResource(R.drawable.bg_unit)
            binding.KG.setBackgroundResource(R.color.transfer)
            binding.LB.setBackgroundResource(R.color.transfer)
            binding.rulerWeight.initViewParam(20f, 0f, 48f, 0.1f)
            binding.rulerWeight.setUnit(getString(R.string.st))
            handler.postDelayed({ isClick = true }, 500)
        }
    }

    private val kgClickListener = View.OnClickListener { v ->
        if (isClick) {
            isClick = true
            checkSt = false
            checkLb = false
            checkKg = true
            binding.KG.setBackgroundResource(R.drawable.bg_unit)
            binding.ST.setBackgroundResource(R.color.transfer)
            binding.LB.setBackgroundResource(R.color.transfer)
            binding.rulerWeight.initViewParam(50f, 1f, 300f, 0.1f)
            binding.rulerWeight.setUnit(getString(R.string.kg))
            handler.postDelayed({ isClick = true }, 500)
        }
    }

    private val lbClickListener = View.OnClickListener { v ->
        if (isClick) {
            isClick = true
            checkSt = false
            checkLb = true
            checkKg = false
            binding.LB.setBackgroundResource(R.drawable.bg_unit)
            binding.ST.setBackgroundResource(R.color.transfer)
            binding.KG.setBackgroundResource(R.color.transfer)
            binding.rulerWeight.initViewParam(50f, 2.2f, 663f, 0.1f)
            binding.rulerWeight.setUnit(getString(R.string.lb))
            handler.postDelayed({ isClick = true }, 500)
        }
    }
    override fun initData() {
        checkCm = true
        checkKg = false
        checkLb = false
        checkSt = true
        genders = FEMALE
        binding.rulerHeight.initViewParam(50f, 30f, 300f, 0.1f)
        binding.rulerWeight.initViewParam(20f, 0f, 48f, 0.1f)
        binding.rulerHeight.setUnit(getString(R.string.cm))
        binding.rulerWeight.setUnit(getString(R.string.st))
        date = getCurrentDateFormatted()
        dialogDate = DialogDate(requireContext(), this)
        handler = Handler(Looper.getMainLooper())
    }

    override fun initView() {
        setupNumberPicker()
    }

    override fun initListener() {
        binding.imgRecent.setOnClickListener(recentClickListener)
        binding.imgReload.setOnClickListener(reloadClickListener)
        binding.Male.setOnClickListener(maleClickListener)
        binding.Female.setOnClickListener(femaleClickListener)
        binding.btnCalculator.setOnClickListener(calculatorClickListener)
        binding.CM.setOnClickListener(cmClickListener)
        binding.FT.setOnClickListener(ftClickListener)
        binding.ST.setOnClickListener(stClickListener)
        binding.KG.setOnClickListener(kgClickListener)
        binding.LB.setOnClickListener(lbClickListener)
        binding.Time.setOnClickListener(timeClickListener)
    }
    private fun getCurrentDateFormatted(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    override fun clickDateListener(date: String?) {

    }

    override fun hideNavigation() {

    }

}