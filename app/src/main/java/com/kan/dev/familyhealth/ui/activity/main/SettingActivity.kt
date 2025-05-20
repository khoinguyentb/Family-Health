package com.kan.dev.familyhealth.ui.activity.main

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.adapter.SettingAdapter
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.Data.Companion.settingModelList
import com.kan.dev.familyhealth.databinding.ActivitySettingBinding
import com.kan.dev.familyhealth.interfacces.ISettingClickListener
import com.kan.dev.familyhealth.ui.activity.VersionActivity
import com.kan.dev.familyhealth.ui.activity.authen.SignInActivity
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick
import com.kan.dev.familyhealth.utils.shareApp
import com.kan.dev.st048_bmicalculator.dialog.DialogRate
import com.kan.dev.st048_bmicalculator.dialog.OnDialogDismissListener


class SettingActivity : BaseActivity<ActivitySettingBinding>(),ISettingClickListener,OnDialogDismissListener {
    override fun setViewBinding(): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layoutInflater)
    }
    private lateinit var adapter: SettingAdapter
    private lateinit var dialogRate: DialogRate
    override fun initData() {
        adapter = SettingAdapter(this)
        adapter.setItems(settingModelList)
        if (sharePre.isRated(this)) {
            adapter.removeItem(2)
        }
        binding.apply {
            rcvSetting.adapter = adapter
            rcvSetting.layoutManager =
                LinearLayoutManager(this@SettingActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun initView() {
        
    }

    override fun initListener() {
        binding.apply {
            icBack.setOnClickListener {
                finish()
            }
        }
    }

    override fun clickItemSetting(image: Int) {
        if (isClick) {
            isClick = false
            when (image) {
                R.drawable.language -> {
                    startActivity(Intent(this, LanguageActivity::class.java))
                }
                R.drawable.share -> {
                    shareApp(this)
                }
                R.drawable.rate -> {
                    dialogRate = DialogRate(this,this)
                    dialogRate.show()
                }
                R.drawable.version -> {
                    startActivity(Intent(this, VersionActivity::class.java))
                }
                R.drawable.logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, SignInActivity::class.java))
                    finishAffinity()
                }
                R.drawable.friend -> {
                    startActivity(Intent(this, FriendActivity::class.java))
                }
            }
            handler.postDelayed(Runnable { isClick = true }, 500)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDialogDismiss() {
        if (sharePre.isRated(this)) {
            adapter = SettingAdapter(this)
            adapter.setItems(settingModelList)
            adapter.removeItem(2)
            binding.rcvSetting.adapter = adapter
        }
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
}