package com.kan.dev.familyhealth.ui.activity.main

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.databinding.ActivitySplashBinding
import com.kan.dev.familyhealth.utils.IS_LANGUAGE
import com.kan.dev.familyhealth.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun setViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }
    private lateinit var mAuth: FirebaseAuth
    private val viewModel : SplashViewModel by viewModels()

    override fun initData() {
        mAuth = FirebaseAuth.getInstance()
        viewModel.isRateApp()
    }

    override fun initView() {
//        val currentUser = mAuth.currentUser
//        if (currentUser != null) {
//
//        }

        if (!isTaskRoot && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action != null && intent.action.equals(
                Intent.ACTION_MAIN
            )
        ) {
            finish()
            return
        }
        inten()
    }


    override fun initListener() {
    }
    override fun onResume() {
        super.onResume()
    }
    fun inten() {
        startActivity()
    }

    private fun startActivity(){

        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }else{
            if (!sharePre.getBoolean(IS_LANGUAGE,false)){
                startActivity(Intent(this, LanguageActivity::class.java))
            }else{
                startActivity(Intent(this, IntroActivity::class.java))
            }
        }
        finish()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

    }
}