package com.kan.dev.familyhealth.ui.activity.main

import android.content.Intent
import com.kan.dev.familyhealth.adapter.IntroAdapter
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.Data.Companion.introModelList
import com.kan.dev.familyhealth.databinding.ActivityIntroBinding
import com.kan.dev.familyhealth.ui.activity.PermissionActivity
import com.kan.dev.familyhealth.ui.activity.authen.SignInActivity
import com.kan.dev.familyhealth.utils.CHECK_PER
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroActivity : BaseActivity<ActivityIntroBinding>() {
    override fun setViewBinding(): ActivityIntroBinding {
        return ActivityIntroBinding.inflate(layoutInflater)
    }

    private lateinit var adapter : IntroAdapter
    private lateinit var intent: Intent
    override fun initData() {
        adapter = IntroAdapter(this)
        adapter.setItems(introModelList)
    }

    override fun initView() {
        binding.apply {
            viewpagerIntro.adapter = adapter
            wormDotsIndicator.attachTo(viewpagerIntro)
        }
    }

    override fun initListener() {
        binding.apply {
            Next.setOnClickListener{
                if (isClick) {
                    isClick = false
                    val page: Int = viewpagerIntro.getCurrentItem()
                    if (page == 2) {
                        if (sharePre.getBoolean(CHECK_PER,false)){
                            intent = Intent(this@IntroActivity, SignInActivity::class.java)
                        }else{
                            intent = Intent(this@IntroActivity, PermissionActivity::class.java)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        viewpagerIntro.currentItem = page + 1
                    }
                    handler.postDelayed(Runnable { isClick = true }, 500)
                }
            }
        }
    }
}