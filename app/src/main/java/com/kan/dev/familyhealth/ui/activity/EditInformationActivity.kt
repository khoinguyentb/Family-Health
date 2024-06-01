package com.kan.dev.familyhealth.ui.activity

import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.databinding.ActivityEditInformationBinding
import com.kan.dev.familyhealth.dialog.DialogDate
import com.lvt.ads.util.Admob

class EditInformationActivity : BaseActivity<ActivityEditInformationBinding>() {
    override fun setViewBinding(): ActivityEditInformationBinding {
        return ActivityEditInformationBinding.inflate(layoutInflater)
    }

    private var avt : Int = 0
    private var code: String? = null
    private var name: String? = null
    private var phoneNumber:String? = null
    private var dateOfBirth : String? = null
    private var weight : Float? = null
    private var height : Float? = null
    private var sex:String? = null
    private var checkCm: Boolean = false
    private var checkSt: Boolean = false
    private var checkKg: Boolean = false
    private var checkLb: Boolean = false
    private lateinit var dialogDate: DialogDate
    private lateinit var myCode : String
    override fun initData() {

    }

    override fun initView() {
        Admob.getInstance().loadBannerFragment(this, getString(R.string.banner_all), binding.includeBanner)
    }

    override fun initListener() {

    }

}