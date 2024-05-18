package com.kan.dev.familyhealth.ui.activity.interaction

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRadioButton
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.zxing.WriterException
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.RealtimeDAO
import com.kan.dev.familyhealth.data.RealtimeDAO.checkExistUser
import com.kan.dev.familyhealth.data.RealtimeDAO.generateMyCode
import com.kan.dev.familyhealth.data.RealtimeDAO.initRealtimeData
import com.kan.dev.familyhealth.data.RealtimeDAO.pushRealtimeData
import com.kan.dev.familyhealth.databinding.ActivityInformationBinding
import com.kan.dev.familyhealth.ui.activity.MainActivity
import com.kan.dev.familyhealth.utils.FEMALE
import com.kan.dev.familyhealth.utils.KEY_QR_BITMAP
import com.kan.dev.familyhealth.utils.MALE
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.OTHER
import com.kan.dev.familyhealth.utils.PHONE_PATTERN
import com.kan.dev.familyhealth.utils.generateQRCode
import com.kan.dev.familyhealth.utils.isClick
import com.kan.dev.familyhealth.utils.toastDuration
import com.lvt.ads.callback.InterCallback
import com.lvt.ads.callback.NativeCallback
import com.lvt.ads.util.Admob
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class InformationActivity : BaseActivity<ActivityInformationBinding>() {
    override fun setViewBinding(): ActivityInformationBinding {
        return ActivityInformationBinding.inflate(layoutInflater)
    }
    var lastTime: Long = 0
    private var code: String? = null
    private var name: String? = null
    private var phoneNumber:String? = null
    private var dateOfBirth : String? = null
    private var weight : Float? = null
    private var height : Float? = null
    private var sex:String? = null
    var listID = arrayListOf<String>()
    override fun initData() {
        binding.apply {
            Admob.getInstance()
                .loadNativeAd(this@InformationActivity, getString(R.string.native_all), object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        super.onNativeAdLoaded(nativeAd)
                        val adView = LayoutInflater.from(
                            applicationContext
                        ).inflate(
                                com.lvt.ads.R.layout.ads_native_small_btn_ads_bottom,
                                null
                            ) as NativeAdView
                        binding.nativeAds.removeAllViews()
                        binding.nativeAds.addView(adView)
                        Admob.getInstance().pushAdsToViewCustom(nativeAd, adView)
                    }

                    override fun onAdFailedToLoad() {
                        super.onAdFailedToLoad()
                        binding.nativeAds.visibility = View.INVISIBLE
                    }
                })
            initRealtimeData()
            actionOnTextChange()
            sex = MALE
            code = generateMyCode()
            checkExistUser(code,RealtimeDAO.valueEventListener{snapshot->
                if (snapshot!!.exists()) code = generateMyCode()
            })

            val qrCodeWidthAndHeight = 500
            val tempFile = File(externalCacheDir, "qr_code.png")

            try {
                val bitmap: Bitmap = generateQRCode(applicationContext, code, qrCodeWidthAndHeight)
                val outputStream = FileOutputStream(tempFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream)
                outputStream.flush()
                outputStream.close()
                sharePre.putString(KEY_QR_BITMAP, tempFile.path)
            } catch (e: WriterException) {
                throw RuntimeException(e)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

    override fun initView() {

    }

    override fun initListener() {
        binding.btnSave.setOnClickListener { view ->
            if (isClick) {
                isClick = false
                actionSave()
                Handler().postDelayed({ isClick = true }, 500)
            }
        }
        binding.layoutMale.setOnClickListener { view ->
            actionChooseGender(
                MALE,
                binding.rbMale,
                binding.txtMale
            )
        }
        binding.layoutFemale.setOnClickListener { view ->
            actionChooseGender(
                FEMALE,
                binding.rbFemale,
                binding.txtFemale
            )
        }
        binding.layoutOther.setOnClickListener { view ->
            actionChooseGender(
                OTHER,
                binding.rbOther,
                binding.txtOther
            )
        }

    }

    private fun actionChooseGender(s: String, rb: AppCompatRadioButton, tv: TextView) {
        sex = s
        binding.apply {
            rbMale.isChecked = false
            rbFemale.isChecked = false
            rbOther.isChecked = false
            txtMale.setTypeface(null, Typeface.NORMAL)
            txtFemale.setTypeface(null, Typeface.NORMAL)
            txtOther.setTypeface(null, Typeface.NORMAL)
            rb.isChecked = true
        }

    }

    private fun actionOnTextChange() {
        binding.edtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length > 25 && i2 > 0) {
                    binding.edtName.setText(binding.edtName.text.delete(25, 26))
                    binding.edtName.setSelection(binding.edtName.length())
                    if (System.currentTimeMillis() - lastTime > toastDuration) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.over_25_char),
                            Toast.LENGTH_SHORT
                        ).show()
                        lastTime = System.currentTimeMillis()
                    }
                } else {
                    name = charSequence.toString().trim { it <= ' ' }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding.edtPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                phoneNumber = charSequence.toString().trim { it <= ' ' }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding.edtPhoneNumber.setOnEditorActionListener { textView, i, keyEvent ->
            if (i === EditorInfo.IME_ACTION_DONE) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                val imm =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(textView.windowToken, 0)
                binding.edtPhoneNumber.clearFocus()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun actionSave() {
        if (binding.edtPhoneNumber.text.toString().trim().isEmpty()) {
            if (System.currentTimeMillis() - lastTime > toastDuration) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.enter_phone_number),
                    Toast.LENGTH_SHORT
                ).show()
                lastTime = System.currentTimeMillis()
                binding.edtPhoneNumber.requestFocus()
            }
        } else if (binding.edtName.text.toString().trim().isEmpty()) {
            if (System.currentTimeMillis() - lastTime > toastDuration) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.enter_name),
                    Toast.LENGTH_SHORT
                ).show()
                lastTime = System.currentTimeMillis()
                binding.edtName.requestFocus()
            }
        } else if (!phoneNumber!!.matches(PHONE_PATTERN.toRegex())) {
            if (System.currentTimeMillis() - lastTime > toastDuration) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.invalid_phone_number),
                    Toast.LENGTH_SHORT
                ).show()
                lastTime = System.currentTimeMillis()
            }
        } else {
            weight = binding.rulerWeight.value
            height = binding.rulerHeight.value
            Admob.getInstance().setOpenActivityAfterShowInterAds(false)
            Admob.getInstance().showInterAll(this, object : InterCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    val users: MutableMap<String, Any> = HashMap()
                    users.put("name", name!!)
                    users.put("phoneNumber", phoneNumber!!)
                    users.put("weight", weight!!)
                    users.put("height", height!!)
                    users.put("dateOfBirth", dateOfBirth!!)
                    users.put("gender",sex!!)
                    pushRealtimeData(code!!,users,RealtimeDAO.onSuccessListener{result ->
                        sharePre.putString(MY_CODE,code)
                        startActivity(Intent(this@InformationActivity, MainActivity::class.java))
                        finish()
                    })
                }
            })
        }
    }

}