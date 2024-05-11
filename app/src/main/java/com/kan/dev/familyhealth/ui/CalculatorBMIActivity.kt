package com.kan.dev.familyhealth.ui

import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.model.BMI
import com.kan.dev.familyhealth.databinding.ActivityCalculatorBmiactivityBinding
import com.kan.dev.familyhealth.utils.BMIS
import com.kan.dev.familyhealth.viewmodel.BMIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.pow

@AndroidEntryPoint
class CalculatorBMIActivity : BaseActivity<ActivityCalculatorBmiactivityBinding>() {


    private lateinit var bmi: BMI
    private var weightMin = 47.3
    private var weightMax = 63.9
    private var weightDifference = 0.0
    private var weightUnit: String? = null

    private val viewModel : BMIViewModel by viewModels()

    override fun setViewBinding(): ActivityCalculatorBmiactivityBinding {
        return ActivityCalculatorBmiactivityBinding.inflate(layoutInflater)
    }

    override fun initData() {
        if (intent.hasExtra(BMIS)) {
            bmi = intent.getSerializableExtra(BMIS) as BMI
        }
        var height = 0.0
        val weight: Float = bmi.weight
        height = if (bmi.checkCm) {
            bmi.height / 100.toDouble()
        }else {
            bmi.height * 0.3048
        }

        if (bmi.checkKg) {
            weightMin = 18.5 * height.pow(2.0)
            weightMax = 25 * height.pow(2.0)
            weightUnit = getString(R.string.kg)
        } else if (bmi.checkLb) {
            weightMin = 18.5 * height.pow(2.0) * 2.205
            weightMax = 25 * height.pow(2.0) * 2.205
            weightUnit = getString(R.string.lb)
        } else if (bmi.checkSt) {
            weightMin = 18.5 * height.pow(2.0) * 0.157473
            weightMax = 25 * height.pow(2.0) * 0.157473
            weightUnit = getString(R.string.st)
        }

        if (bmi.age >= 21) {
            if (bmi.bmi< 16) {
                binding.tvStatusBmi.text = getString(R.string.very_Severely_underweight)
                binding.tvStatusBmi.setTextColor(getColor(R.color.very_severely_underweight))
            } else if (bmi.bmi >= 16 && bmi.bmi < 16.9) {
                binding.tvStatusBmi.text = getString(R.string.severely_underweight)
                binding.tvStatusBmi.setTextColor(getColor(R.color.severely_underweight))
            } else if (bmi.bmi >= 17 && bmi.bmi < 18.4) {
                binding.tvStatusBmi.text = getString(R.string.underweight)
                binding.tvStatusBmi.setTextColor(getColor(R.color.underweight))
            } else if (bmi.bmi >= 18.5 && bmi.bmi < 24.9) {
                binding.tvStatusBmi.text = getString(R.string.normal)
                binding.tvStatusBmi.setTextColor(getColor(R.color.normal))
            } else if (bmi.bmi >= 25 && bmi.bmi < 29.9) {
                binding.tvStatusBmi.text = getString(R.string.overweight)
                binding.tvStatusBmi.setTextColor(getColor(R.color.overweight))
            } else if (bmi.bmi >= 30 && bmi.bmi < 34.9) {
                binding.tvStatusBmi.text = getString(R.string.obese_Class_I)
                binding.tvStatusBmi.setTextColor(getColor(R.color.obese_class_I))
            } else if (bmi.bmi >= 35 && bmi.bmi < 39.9) {
                binding.tvStatusBmi.text = getString(R.string.obese_Class_II)
                binding.tvStatusBmi.setTextColor(getColor(R.color.obese_class_II))
            } else if (bmi.bmi > 40) {
                binding.tvStatusBmi.text = getString(R.string.obese_class_III)
                binding.tvStatusBmi.setTextColor(getColor(R.color.obese_class_III))
            }
        } else if (bmi.age <= 20) {
            if (bmi.checkKg) {
                weightMin = 15.4 * height.pow(2.0)
                weightMax = 22.6 * height.pow(2.0)
            } else if (bmi.checkLb) {
                weightMin = 15.4 * height.pow(2.0) * 2.205
                weightMax = 22.6 * height.pow(2.0) * 2.205
            } else if (bmi.checkSt) {
                weightMin = 15.4 * height.pow(2.0) * 0.157473
                weightMax = 22.6 * height.pow(2.0) * 0.157473
            }
            if (bmi.bmi < 15.4) {
                binding.tvStatusBmi.text = getString(R.string.underweight)
                binding.tvStatusBmi.setTextColor(getColor(R.color.underweight20))
            } else if (bmi.bmi >= 15.4 && bmi.bmi < 22.5) {
                binding.tvStatusBmi.text = getString(R.string.normal)
                binding.tvStatusBmi.setTextColor(getColor(R.color.normal20))
            } else if (bmi.bmi >= 22.6 && bmi.bmi < 26.3) {
                binding.tvStatusBmi.text = getString(R.string.overweight)
                binding.tvStatusBmi.setTextColor(getColor(R.color.overweight20))
            } else if (bmi.bmi >= 26.4) {
                binding.tvStatusBmi.text = getString(R.string.obese_Class_I)
                binding.tvStatusBmi.setTextColor(getColor(R.color.obese_class_I20))
            }
        }
        weightMin = weightMin.toString().replace(",", ".").toDouble()
        weightMin = Math.round(weightMin * 100) / 100.0
        weightMax = weightMax.toString().replace(",", ".").toDouble()
        weightMax = Math.round(weightMax * 100) / 100.0
        weightDifference = if (weight in weightMin..weightMax
        ) {
            0.0
        } else if (weight < weightMin) {
            weightMin - weight
        } else {
            weight - weightMax
        }
    }

    override fun initView() {

    }

    override fun initListener() {
        binding.apply {
            icBack.setOnClickListener {  finish() }
            icRecent.setOnClickListener {
                startActivity(Intent(this@CalculatorBMIActivity, RecentActivity::class.java))
            }
            btnSave.setOnClickListener {
                viewModel.insert(bmi)
                Toast.makeText(this@CalculatorBMIActivity, getString(R.string.SavedSuccessfully), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

}