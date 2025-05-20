package com.kan.dev.familyhealth.ui.activity.main

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.RealtimeDAO
import com.kan.dev.familyhealth.databinding.ActivityDetailInformationBinding
import com.kan.dev.familyhealth.ui.activity.BMI.BMIInformationActivity
import com.kan.dev.familyhealth.ui.activity.ExerciseActivity
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailInformationActivity : BaseActivity<ActivityDetailInformationBinding>() {
    override fun setViewBinding(): ActivityDetailInformationBinding {
        return ActivityDetailInformationBinding.inflate(layoutInflater)
    }
    private var code: String? = null
    private var path: String? = null
    private lateinit var myCodee : String
    private lateinit var weightText : String
    private lateinit var heightText : String
    private lateinit var intentInformation : Intent
    override fun initData() {
        code = intent.getStringExtra("receiveCode")
        myCodee = sharePre.getString(MY_CODE,"")!!

        path = if (code == myCodee) {
            myCodee
        } else {
            myCodee + "/friends/" + code
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        RealtimeDAO.getOnetimeData(path) { snapshot ->
            val avt: Int = snapshot!!.child("avt").getValue(Int::class.java)!!
            val battery: Int = snapshot.child("battery").getValue(Int::class.java)!!
            val name: String = snapshot.child("name").getValue(String::class.java)!!
            val phone: String =
                snapshot.child("phoneNumber").getValue(String::class.java)!!
            val gender: String = snapshot.child("gender").getValue(String::class.java)!!
            val checkCm = snapshot.child("checkCm").getValue(Boolean::class.java)!!
            val checkKg = snapshot.child("checkKg").getValue(Boolean::class.java)!!
            val checkLb = snapshot.child("checkLb").getValue(Boolean::class.java)!!
            val checkSt = snapshot.child("checkSt").getValue(Boolean::class.java)!!
            val weight = snapshot.child("weight").getValue(Float::class.java)!!
            val height = snapshot.child("height").getValue(Float::class.java)!!

            if (checkCm){
                heightText = "$height Cm"
            }

            weightText = if (checkKg){
                "$weight Kg"
            }else if (checkLb){
                "$weight Lb"
            }else{
                "$weight St"
            }

            binding.txtWeight.text = weightText
            binding.txtHeight.text = heightText

            if (code == myCodee) {
                binding.imgUser.setImageResource(avt)
                binding.txtBattery.text = "$battery%"
                binding.txtName.text = name
                binding.txtPhoneNum.text = phone
                binding.txtSex.text = gender
                when (battery) {
                    in 1..20 -> {
                        binding.icPin.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.fa_batteryic_pin_1
                            )
                        )
                    }
                    in 21..34 -> {
                        binding.icPin.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.fa_batteryic_pin_2
                            )
                        )
                    }
                    in 35..59 -> {
                        binding.icPin.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.fa_batteryic_pin_3
                            )
                        )
                    }
                    in 61..84 -> {
                        binding.icPin.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.fa_batteryic_pin_4
                            )
                        )
                    }
                    else -> {
                        binding.icPin.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.fa_batteryic_pin_5
                            )
                        )
                    }
                }
            } else {
                val isTracking = snapshot.child("isTracking").getValue(Boolean::class.java)!!
                val visible = snapshot.child("visible").getValue(Boolean::class.java)!!
                val online = snapshot.child("online").getValue(Boolean::class.java)!!
                if (visible && isTracking && online) {
                    binding.imgOnline.visibility = View.VISIBLE
                } else {
                    binding.imgOnline.visibility = View.INVISIBLE
                    binding.battery.visibility = View.INVISIBLE
                }
                binding.imgUser.setImageResource(avt)
                binding.txtBattery.text = "$battery%"
                binding.txtName.text = name
                binding.txtPhoneNum.text = phone
                binding.txtSex.text = gender
                when (battery) {
                    in 1..20 -> {
                        binding.icPin.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.fa_batteryic_pin_1
                            )
                        )
                    }
                    in 21..34 -> {
                        binding.icPin.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.fa_batteryic_pin_2
                            )
                        )
                    }
                    in 35..59 -> {
                        binding.icPin.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.fa_batteryic_pin_3
                            )
                        )
                    }
                    in 61..84 -> {
                        binding.icPin.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.fa_batteryic_pin_4
                            )
                        )
                    }
                    else -> {
                        binding.icPin.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.fa_batteryic_pin_5
                            )
                        )
                    }
                }
            }
        }
    }

    override fun initListener() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
            btnHealthInformation.setOnClickListener {
                if (isClick){
                    isClick = false
                    intentInformation = Intent(this@DetailInformationActivity,ExerciseActivity::class.java)
                    intentInformation.putExtra("CODE",code)
                    startActivity(intentInformation)
                    handler.postDelayed({ isClick = true},500)
                }
            }
            btnBMIInformation.setOnClickListener {
                if (isClick){
                    isClick = false
                    intentInformation = Intent(this@DetailInformationActivity,BMIInformationActivity::class.java)
                    intentInformation.putExtra("CODE",code)
                    startActivity(intentInformation)
                    handler.postDelayed({ isClick = true},500)
                }
            }
        }
    }

}