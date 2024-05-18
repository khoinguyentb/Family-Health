package com.kan.dev.familyhealth.ui.activity.interaction

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.RealtimeDAO
import com.kan.dev.familyhealth.databinding.ActivityJoinWithFamilyBinding
import com.kan.dev.familyhealth.ui.activity.MainActivity
import com.kan.dev.familyhealth.ui.activity.QRScanActivity
import com.kan.dev.familyhealth.utils.CODE_LENGTH
import com.kan.dev.familyhealth.utils.QR_REQUEST_CODE
import com.kan.dev.familyhealth.utils.isClick
import com.lvt.ads.callback.NativeCallback
import com.lvt.ads.util.Admob

class JoinWithFamilyActivity : BaseActivity<ActivityJoinWithFamilyBinding>() {
    override fun setViewBinding(): ActivityJoinWithFamilyBinding {
        return ActivityJoinWithFamilyBinding.inflate(layoutInflater)
    }
    private var friendCode: String? = null
    private val lastTime: Long = 0
    override fun initData() {
        
    }

    override fun initView() {
        Admob.getInstance()
            .loadNativeAd(this, getString(R.string.native_all), object : NativeCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAd) {
                    super.onNativeAdLoaded(nativeAd)
                    val adView = LayoutInflater.from(
                        applicationContext
                    )
                        .inflate(R.layout.ads_native_small_btn_ads_bottom, null) as NativeAdView
                    binding.nativeAds.removeAllViews()
                    binding.nativeAds.addView(adView)
                    Admob.getInstance().pushAdsToViewCustom(nativeAd, adView)
                }

                override fun onAdFailedToLoad() {
                    super.onAdFailedToLoad()
                    binding.nativeAds.visibility = View.INVISIBLE
                }
            })
        RealtimeDAO.initRealtimeData()
        binding.btnStartJoin.isSelected = true
        binding.btnContinue.isSelected = true
        onTextChange()
    }

    override fun initListener() {
        binding.apply {
            btnScan.setOnClickListener { view ->
                startActivityForResult(
                    Intent(
                        applicationContext,
                        QRScanActivity::class.java
                    ), QR_REQUEST_CODE
                )
            }
            edtCode.setOnEditorActionListener { textView, i, keyEvent ->
                if (i == EditorInfo.IME_ACTION_DONE) {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    val imm =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(textView.windowToken, 0)
                    edtCode.clearFocus()
                    return@setOnEditorActionListener true
                }
                false
            }
            btnStartJoin.setOnClickListener {
                if (isClick) {
                    isClick = false
                    actionJoinFriend()

                }
            }
            btnContinue.setOnClickListener {
                if (isClick) {
                    isClick = false
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                    Handler().postDelayed({ isClick = true }, 500)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        binding.edtCode.clearFocus()
    }

    private fun onTextChange() {
        binding.edtCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                binding.txtHintCode.visibility =
                    if (charSequence.isEmpty()) View.VISIBLE else View.GONE
                binding.btnStartJoin.backgroundTintList =
                    if (charSequence.length == CODE_LENGTH) null else ColorStateList.valueOf(
                        getColor(R.color.gray_B0B9C8)
                    )
                binding.btnStartJoin.isEnabled = charSequence.length == CODE_LENGTH
                friendCode = charSequence.toString().trim { it <= ' ' }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun actionJoinFriend(){
        if (friendCode == null || friendCode!!.length < CODE_LENGTH) {
            hideKeyboard()
            binding.edtCode.clearFocus()
        }else{

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == QR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                assert(data != null)
                if (data!!.getStringExtra("code")!!.isNotEmpty()) {
                    binding.edtCode.setText(data.getStringExtra("code"))
                }
            }
        }
    }

}