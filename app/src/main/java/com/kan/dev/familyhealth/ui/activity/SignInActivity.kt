package com.kan.dev.familyhealth.ui.activity

import android.content.Intent
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.databinding.ActivitySignInBinding

class SignInActivity : BaseActivity<ActivitySignInBinding>() {
    override fun setViewBinding(): ActivitySignInBinding {
        return ActivitySignInBinding.inflate(layoutInflater)
    }

    private lateinit var intent: Intent

    override fun initData() {
    }

    override fun initView() {
    }

    override fun initListener() {
        binding.apply {
            btnSignUp.setOnClickListener {
                intent = Intent(this@SignInActivity,SignUpActivity::class.java)
                startActivity(intent)
            }
        }
    }
}