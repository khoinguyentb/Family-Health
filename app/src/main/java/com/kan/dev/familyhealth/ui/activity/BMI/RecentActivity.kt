package com.kan.dev.familyhealth.ui.activity.BMI

import android.content.Intent
import androidx.activity.viewModels
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.adapter.RecentAdapter
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.model.BMI
import com.kan.dev.familyhealth.databinding.ActivityRecentBinding
import com.kan.dev.familyhealth.interfacces.IRecentListener
import com.kan.dev.familyhealth.utils.BMIS
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick
import com.kan.dev.familyhealth.viewmodel.BMIViewModel
import com.lvt.ads.callback.InterCallback
import com.lvt.ads.util.Admob
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecentActivity : BaseActivity<ActivityRecentBinding>(),IRecentListener {
    override fun setViewBinding(): ActivityRecentBinding {
        return ActivityRecentBinding.inflate(layoutInflater)
    }
    private val viewModel :BMIViewModel by viewModels()
    private val adapter : RecentAdapter by lazy {
        RecentAdapter(this,this)
    }
    override fun initData() {
        viewModel.getAll.observe(this){

        }
    }

    override fun initView() {
        Admob.getInstance().loadCollapsibleBanner(this, getString(R.string.banner_collap))
    }

    override fun initListener() {
        
    }

    override fun deleteRecent(bmi: BMI) {
        viewModel.delete(bmi)
    }

    override fun clickRecent(item: BMI) {
        if (isClick) {
            isClick = false
            Admob.getInstance().showInterAll(this, object : InterCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    item.isRecent = true
                    intent = Intent(this@RecentActivity, CalculatorBMIActivity::class.java)
                    intent!!.putExtra(BMIS, item)
                    startActivity(intent)
                }
            })
            handler.postDelayed(Runnable { isClick = true }, 500)
        }
    }
}