package com.kan.dev.familyhealth.ui.activity

import android.content.Intent
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.databinding.ActivityMainBinding
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
        }
    }
}