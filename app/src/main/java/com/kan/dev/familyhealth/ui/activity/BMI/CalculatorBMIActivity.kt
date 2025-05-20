package com.kan.dev.familyhealth.ui.activity.BMI

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.RealtimeDAO
import com.kan.dev.familyhealth.data.model.BMI
import com.kan.dev.familyhealth.databinding.ActivityCalculatorBmiactivityBinding
import com.kan.dev.familyhealth.dialog.DialogDelete
import com.kan.dev.familyhealth.interfacces.IDeleteClickListener
import com.kan.dev.familyhealth.utils.BMIS
import com.kan.dev.familyhealth.utils.FEMALE
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.isClick
import com.kan.dev.familyhealth.viewmodel.BMIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.pow

@AndroidEntryPoint
class CalculatorBMIActivity : BaseActivity<ActivityCalculatorBmiactivityBinding>(),IDeleteClickListener {

    private lateinit var bmi: BMI
    private var weightMin = 47.3
    private var weightMax = 63.9
    private var weightDifference = 0.0
    private var weightUnit: String? = null
    private var dialogDelete: DialogDelete? = null
    private val viewModel : BMIViewModel by viewModels()
    private var height = 0f
    private var weight = 0f
    private val myCode by lazy {
        sharePre.getString(MY_CODE,"")
    }
    override fun setViewBinding(): ActivityCalculatorBmiactivityBinding {
        return ActivityCalculatorBmiactivityBinding.inflate(layoutInflater)
    }

    override fun initData() {
        if (intent.hasExtra(BMIS)) {
            bmi = intent.getSerializableExtra(BMIS) as BMI
        }
        if (intent.hasExtra("BMIInformation")){
            bmi = intent.getSerializableExtra("BMIInformation") as BMI
            binding.apply {
                btnSave.visibility = View.GONE
                icRecent.visibility = View.GONE
            }
        }
        dialogDelete = DialogDelete(this, this)
        height = 0f
        weight = bmi.weight
        height = if (bmi.checkCm) {
            bmi.height / 100.toFloat()
        }else {
            bmi.height * 0.3048.toFloat()
        }

        if (bmi.checkKg) {
            weightMin = 18.5 * height.pow(2f)
            weightMax = (25 * height.pow(2f)).toDouble()
            weightUnit = getString(R.string.kg)
        } else if (bmi.checkLb) {
            weightMin = 18.5 * height.pow(2f) * 2.205
            weightMax = 25 * height.pow(2f) * 2.205
            weightUnit = getString(R.string.lb)
        } else if (bmi.checkSt) {
            weightMin = 18.5 * height.pow(2f) * 0.157473
            weightMax = 25 * height.pow(2f) * 0.157473
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
                weightMin = 15.4 * height.pow(2f)
                weightMax = 22.6 * height.pow(2f)
            } else if (bmi.checkLb) {
                weightMin = 15.4 * height.pow(2f) * 2.205
                weightMax = 22.6 * height.pow(2f) * 2.205
            } else if (bmi.checkSt) {
                weightMin = 15.4 * height.pow(2f) * 0.157473
                weightMax = 22.6 * height.pow(2f) * 0.157473
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
        binding.apply {
            if (bmi.isRecent) {
                icRecent.setImageResource(R.drawable.icdelete)
                btnSave.visibility = View.GONE
            } else {
                icRecent.setImageResource(R.drawable.recent)
                btnSave.visibility = View.VISIBLE
            }

            binding.tvStatusBmi.isSelected = true
            binding.tvBmi.text = bmi.bmi.toString()
            binding.tvNormalWeightIndex.text = "$weightMin – $weightMax $weightUnit"
            if (weight > weightMax) {
                binding.tvDifferenceIndex.text = "+$weightDifference"
            } else {
                binding.tvDifferenceIndex.text = weightDifference.toString() + ""
            }
            binding.tvTime.text = bmi.time

            if (bmi.gender == FEMALE) {
                binding.imgGender.setImageResource(R.drawable.female)
                binding.tvGender.setText(R.string.Female)
            } else {
                binding.imgGender.setImageResource(R.drawable.male)
                binding.tvGender.setText(R.string.Male)
            }
            binding.tvAgeIndex.text = bmi.age.toString()
            if (bmi.checkCm) {
                binding.tvHeightIndex.text = bmi.height.toString()
                binding.tvHeightUnit.text = getString(R.string.Centimeter)
            } else {
                binding.tvHeightIndex.text = bmi.height.toString()
                binding.tvHeightUnit.text = getString(R.string.Feet)
            }
            if (bmi.checkKg) {
                binding.tvWeightIndex.text = bmi.weight.toString()
                binding.tvWeightUnit.text = getString(R.string.Kilogram)
            } else if (bmi.checkLb) {
                binding.tvWeightIndex.text = bmi.weight.toString()
                binding.tvWeightUnit.text = getString(R.string.Pound)
            } else {
                binding.tvWeightIndex.text = bmi.weight.toString()
                binding.tvWeightUnit.text = getString(R.string.Stone)
            }
        }
    }

    override fun initListener() {
        binding.apply {
            icBack.setOnClickListener {
//                if (bmi.isRecent) {
//                    finish()
//                } else {
//                    dialogDelete!!.show()
//                }
                finish()
            }
            icRecent.setOnClickListener {
                if (isClick) {
                    isClick = false
                    if (bmi.isRecent) {
                        dialogDelete!!.show()
                    } else {
                        val intent = Intent(this@CalculatorBMIActivity, RecentActivity::class.java)
                        startActivity(intent)
                    }
                    Handler(Looper.getMainLooper())
                        .postDelayed({ isClick = true }, 500)
                }
            }
            btnSave.setOnClickListener {
                val bMI = hashMapOf<String, Any>(
                    "id" to bmi.id,
                    "time" to bmi.time,
                    "gender" to bmi.gender,
                    "age" to bmi.age,
                    "weight" to bmi.weight,
                    "checkCm" to bmi.checkCm,
                    "checkSt" to bmi.checkSt,
                    "checkKg" to bmi.checkKg,
                    "checkLb" to bmi.checkLb,
                    "height" to bmi.height,
                    "bmi" to bmi.bmi,
                    "isRecent" to bmi.isRecent
                )
                RealtimeDAO.pushRealtimeData(myCode + "/BMI/" + bmi.id,bMI){
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.SavedSuccessfully),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }

    override fun clickDelete() {
        RealtimeDAO.removeRealtimeData(myCode + "/BMI/" + bmi.id){
            Toast.makeText(this,getString(R.string.DeletedSuccessfully),Toast.LENGTH_SHORT).show()
            viewModel.delete(bmi)
        }
    }

}