package com.kan.dev.familyhealth.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.databinding.ActivitySplashBinding
import com.kan.dev.familyhealth.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun setViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    private val viewModel : SplashViewModel by viewModels()

    private val handel by lazy {
        Handler(Looper.getMainLooper())
    }
    lateinit var runable: Runnable
    private val runnable = Runnable {
        handel.removeCallbacks(runable)
        binding!!.pbLoading.progress = 20
        startActivity()
    }
    override fun initData() {
        viewModel.isRateApp()
    }

    override fun initView() {
    }

    override fun initListener() {
    }
    override fun onStart() {
        super.onStart()
        var a = 0
        binding?.apply {
            pbLoading.max = 20
            runable = Runnable {
                pbLoading.progress = a
                a++
                handel.postDelayed(runable, 100)
            }
            handel.postDelayed(runable,0)
            handel.postDelayed(runnable,2000)
        }
    }
    override fun onStop() {
        super.onStop()
        handel.removeCallbacksAndMessages(null)
    }
    private fun startActivity(){
        intent = if (viewModel.isFirstApp() &&
            viewModel.isFirstAppLang() && viewModel.isFirstAppPer()){
            Intent(this, SignInActivity::class.java)
        }else if (viewModel.isFirstAppLang() &&
            !viewModel.isFirstApp() &&
            !viewModel.isFirstAppPer()){
            Intent(this, IntroActivity::class.java)
        }else if (viewModel.isFirstAppLang()&&
            !viewModel.isFirstApp() &&
            viewModel.isFirstAppPer()){
            Intent(this, PermissionActivity::class.java)
        }else{
            Intent(this, LanguageActivity::class.java)
        }
        intent!!.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

    }
}