package com.kan.dev.familyhealth.ui.activity.authen

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.databinding.ActivityForgetPasswordBinding


class ForgetPasswordActivity : BaseActivity<ActivityForgetPasswordBinding>() {
    override fun setViewBinding(): ActivityForgetPasswordBinding {
        return ActivityForgetPasswordBinding.inflate(layoutInflater)
    }

    override fun initData() {
        
    }

    override fun initView() {
        
    }

    override fun initListener() {
        val auth = FirebaseAuth.getInstance()
        binding.apply {
            icBack.setOnClickListener {
                finish()
            }
            btnReset.setOnClickListener {
                auth.sendPasswordResetEmail(edtUserName.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            finish()
                            Toast.makeText(this@ForgetPasswordActivity,getString(R.string.CheckMail),Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@ForgetPasswordActivity,getString(R.string.EmailValid),Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

}