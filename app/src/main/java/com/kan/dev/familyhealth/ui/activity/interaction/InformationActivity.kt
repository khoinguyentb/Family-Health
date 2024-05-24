package com.kan.dev.familyhealth.ui.activity.interaction

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRadioButton
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.zxing.WriterException
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.RealtimeDAO
import com.kan.dev.familyhealth.data.RealtimeDAO.checkExistUser
import com.kan.dev.familyhealth.data.RealtimeDAO.generateMyCode
import com.kan.dev.familyhealth.data.RealtimeDAO.initRealtimeData
import com.kan.dev.familyhealth.data.RealtimeDAO.pushRealtimeData
import com.kan.dev.familyhealth.databinding.ActivityInformationBinding
import com.kan.dev.familyhealth.dialog.DialogAvt
import com.kan.dev.familyhealth.dialog.OnSaveListener
import com.kan.dev.familyhealth.utils.FEMALE
import com.kan.dev.familyhealth.utils.KEY_QR_BITMAP
import com.kan.dev.familyhealth.utils.MALE
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.OTHER
import com.kan.dev.familyhealth.utils.PHONE_PATTERN
import com.kan.dev.familyhealth.utils.generateQRCode
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.initAvatarList
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
    private var myUser = FirebaseAuth.getInstance().currentUser
    private lateinit var profileUpdates : UserProfileChangeRequest
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
            avt = R.drawable.ic_avt_1
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
        checkCm = true
        checkKg = false
        checkLb = false
        checkSt = true
        binding.rulerHeight.initViewParam(50f, 30f, 300f, 0.5f)
        binding.rulerWeight.initViewParam(20f, 0f, 48f, 0.1f)
        binding.rulerHeight.setUnit(getString(R.string.cm))
        binding.rulerWeight.setUnit(getString(R.string.st))
    }

    override fun initListener() {
        binding.apply {
            btnSave.setOnClickListener { 
                if (isClick) {
                    isClick = false
                    actionSave()
                }
            }
            layoutMale.setOnClickListener { 
                actionChooseGender(
                    MALE,
                    rbMale,
                    txtMale
                )
            }
            layoutFemale.setOnClickListener { 
                actionChooseGender(
                    FEMALE,
                    rbFemale,
                    txtFemale
                )
            }
            layoutOther.setOnClickListener { 
                actionChooseGender(
                    OTHER,
                    rbOther,
                    txtOther
                )
            }

            CM.setOnClickListener{
                if (isClick) {
                    isClick = true
                    checkCm = true
                    CM.setBackgroundResource(R.drawable.bg_unit)
                    FT.setBackgroundResource(R.color.transfer)
                    rulerHeight.initViewParam(50f, 30f, 300f, 0.5f)
                    rulerHeight.setUnit(getString(R.string.cm))
                    handler.postDelayed({ isClick = true }, 500)
                }
            }
            FT.setOnClickListener{
                if (isClick) {
                    isClick = true
                    checkCm = false
                    FT.setBackgroundResource(R.drawable.bg_unit)
                    CM.setBackgroundResource(R.color.transfer)
                    rulerHeight.initViewParam(5f, 0f, 10f, 0.1f)
                    rulerHeight.setUnit(getString(R.string.ft))
                    handler.postDelayed({ isClick = true }, 500)
                }
            }
            ST.setOnClickListener{
                if (isClick) {
                    isClick = true
                    checkSt = true
                    checkLb = false
                    checkKg = false
                    ST.setBackgroundResource(R.drawable.bg_unit)
                    KG.setBackgroundResource(R.color.transfer)
                    LB.setBackgroundResource(R.color.transfer)
                    rulerWeight.initViewParam(20f, 0f, 48f, 0.1f)
                    rulerWeight.setUnit(getString(R.string.st))
                    handler.postDelayed({ isClick = true }, 500)
                }
            }
            KG.setOnClickListener{
                if (isClick) {
                    isClick = true
                    checkSt = false
                    checkLb = false
                    checkKg = true
                    KG.setBackgroundResource(R.drawable.bg_unit)
                    ST.setBackgroundResource(R.color.transfer)
                    LB.setBackgroundResource(R.color.transfer)
                    rulerWeight.initViewParam(50f, 1f, 300f, 0.1f)
                    rulerWeight.setUnit(getString(R.string.kg))
                    handler.postDelayed({ isClick = true }, 500)
                }
            }
            LB.setOnClickListener{
                if (isClick) {
                    isClick = true
                    checkSt = false
                    checkLb = true
                    checkKg = false
                    LB.setBackgroundResource(R.drawable.bg_unit)
                    ST.setBackgroundResource(R.color.transfer)
                    KG.setBackgroundResource(R.color.transfer)
                    rulerWeight.initViewParam(50f, 2.2f, 663f, 0.1f)
                    rulerWeight.setUnit(getString(R.string.lb))
                    handler.postDelayed({ isClick = true }, 500)
                }
            }
        }
    }

    private fun initAvatar() {
        sharePre.putInt("selectedItem", 0)
        binding.imgAvt.setImageResource(avt)
        binding.imgAvt.setOnClickListener {
            val dialogAvt = DialogAvt(this , object : OnSaveListener{
                override fun onClickSave(avt: Int) {
                    for (i in 0 until initAvatarList(this@InformationActivity).size) {
                        if (avt == initAvatarList(this@InformationActivity)[i])
                            sharePre.putInt("selectedItem", i)
                    }
                    binding.imgAvt.setImageResource(avt)
                    this@InformationActivity.avt = avt
                }
            })
            dialogAvt.show()
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
        binding.edtPhoneNumber.setOnEditorActionListener { textView, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
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
            isClick = true
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
            isClick = true
        } else if (!phoneNumber!!.matches(PHONE_PATTERN.toRegex())) {
            if (System.currentTimeMillis() - lastTime > toastDuration) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.invalid_phone_number),
                    Toast.LENGTH_SHORT
                ).show()
                lastTime = System.currentTimeMillis()
            }
            isClick = true
        } else {
            weight = binding.rulerWeight.value
            height = binding.rulerHeight.value
            dateOfBirth = binding.edtDatebirth.text.toString()
            Admob.getInstance().setOpenActivityAfterShowInterAds(false)
            Admob.getInstance().showInterAll(this, object : InterCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    val users = mapOf(
                        "name" to (name ?: ""),
                        "avt" to avt,
                        "phoneNumber" to (phoneNumber ?: ""),
                        "weight" to (weight ?: ""),
                        "height" to (height ?: ""),
                        "dateOfBirth" to (dateOfBirth ?: ""),
                        "gender" to (sex ?: ""),
                        "latLng" to "",
                        "isSos" to false,
                        "isTracking" to true,
                        "lastActive" to "",
                        "checkCm" to checkCm,
                        "checkSt" to checkSt,
                        "checkLb" to checkLb,
                        "checkKg" to checkKg,
                    )

                    pushRealtimeData(code!!,users,RealtimeDAO.onSuccessListener{result ->
                        isClick = true
                        profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(code)
                            .build()
                        myUser!!.updateProfile(profileUpdates)
                            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                                if (task.isSuccessful) {
                                    Log.d("KanMobile", "User profile updated.")
                                }
                            })
                        sharePre.putString(MY_CODE,code)
                        startActivity(Intent(this@InformationActivity, ShareInformationActivity::class.java))
                        finish()
                    })
                }
            })
        }
    }

}