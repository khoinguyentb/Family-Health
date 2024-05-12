package com.kan.dev.familyhealth.ui.activity

import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.databinding.ActivitySignInBinding

class SignInActivity : BaseActivity<ActivitySignInBinding>() {
    override fun setViewBinding(): ActivitySignInBinding {
        return ActivitySignInBinding.inflate(layoutInflater)
    }

    override fun initData() {
    }

    override fun initView() {
    }

    override fun initListener() {
        binding.apply {
            btnSignUp.setOnClickListener {

            }
        }
    }
}