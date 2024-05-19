package com.kan.dev.familyhealth.ui.activity.main

import android.content.Intent
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.databinding.ActivityMainBinding
import com.kan.dev.familyhealth.ui.activity.BMI.BMIActivity
import com.kan.dev.familyhealth.ui.activity.ExerciseActivity
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun setViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initData() {

    }

    override fun initView() {

    }

    override fun initListener() {
        binding.apply {
            btnExercise.setOnClickListener {
                startActivity(Intent(this@MainActivity, ExerciseActivity::class.java))
            }
            btnBMI.setOnClickListener {
                startActivity(Intent(this@MainActivity, BMIActivity::class.java))
            }
            btnMap.setOnClickListener {
                startActivity(Intent(this@MainActivity, BMIActivity::class.java))
            }
            profileImage.setOnClickListener {
                if (isClick){
                    isClick = false
                    startActivity(Intent(this@MainActivity, SettingActivity::class.java))
                    handler.postDelayed({ isClick = true},500)
                }
            }
        }
    }
}