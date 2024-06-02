package com.kan.dev.familyhealth.ui.activity

import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRadioButton
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.RealtimeDAO
import com.kan.dev.familyhealth.data.RealtimeDAO.getOnetimeData
import com.kan.dev.familyhealth.databinding.ActivityEditInformationBinding
import com.kan.dev.familyhealth.dialog.DialogAvt
import com.kan.dev.familyhealth.dialog.DialogDate
import com.kan.dev.familyhealth.dialog.OnSaveListener
import com.kan.dev.familyhealth.utils.FEMALE
import com.kan.dev.familyhealth.utils.MALE
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.OTHER
import com.kan.dev.familyhealth.utils.PHONE_PATTERN
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.initAvatarList
import com.kan.dev.familyhealth.utils.isClick
import com.kan.dev.familyhealth.utils.toastDuration
import com.lvt.ads.callback.InterCallback
import com.lvt.ads.util.Admob

class EditInformationActivity : BaseActivity<ActivityEditInformationBinding>() {
    override fun setViewBinding(): ActivityEditInformationBinding {
        return ActivityEditInformationBinding.inflate(layoutInflater)
    }
    private var lastTime: Long = 0
    private var avt : Int = 0
    private var name: String? = null
    private var phoneNumber:String? = null
    private var dateOfBirth : String? = null
    private var weight : Float? = null
    private var height : Float? = null
    private var gender:String? = null
    private var checkCm: Boolean = false
    private var checkSt: Boolean = false
    private var checkKg: Boolean = false
    private var checkLb: Boolean = false
    private lateinit var dialogDate: DialogDate
    private lateinit var myCode : String
    private lateinit var dialogAvt: DialogAvt
    override fun initData() {
        myCode = sharePre.getString(MY_CODE,"")!!
        binding.apply {
            getOnetimeData(myCode) { snapshot ->
                avt = snapshot!!.child("avt").getValue(Int::class.java)!!
                name = snapshot.child("name").getValue(String::class.java)
                dateOfBirth = snapshot.child("dateOfBirth").getValue(String::class.java)
                this@EditInformationActivity.
                weight = snapshot.child("weight").getValue(Float::class.java)
                height = snapshot.child("height").getValue(Float::class.java)
                checkCm = snapshot.child("checkCm").getValue(Boolean::class.java)!!
                checkSt = snapshot.child("checkSt").getValue(Boolean::class.java)!!
                checkKg = snapshot.child("checkKg").getValue(Boolean::class.java)!!
                checkLb = snapshot.child("checkLb").getValue(Boolean::class.java)!!
                phoneNumber = snapshot.child("phoneNumber").getValue(String::class.java)
                gender = snapshot.child("gender").getValue(String::class.java)
                getAvatar(avt)
                edtName.setText(name)
                edtPhoneNumber.setText(phoneNumber)
                edtDatebirth.text = dateOfBirth
                if (checkCm){
                    CM.setBackgroundResource(R.drawable.bg_unit)
                    FT.setBackgroundResource(R.color.transfer)
                    rulerHeight.initViewParam(this@EditInformationActivity.weight!!, 30f, 300f, 0.5f)
                    rulerHeight.setUnit(getString(R.string.cm))
                }else{
                    FT.setBackgroundResource(R.drawable.bg_unit)
                    CM.setBackgroundResource(R.color.transfer)
                    rulerHeight.initViewParam(this@EditInformationActivity.weight!!, 0f, 10f, 0.1f)
                    rulerHeight.setUnit(getString(R.string.ft))
                }

                if (checkKg){
                    KG.setBackgroundResource(R.drawable.bg_unit)
                    ST.setBackgroundResource(R.color.transfer)
                    LB.setBackgroundResource(R.color.transfer)
                    rulerWeight.initViewParam(height!!, 1f, 300f, 0.1f)
                    rulerWeight.setUnit(getString(R.string.kg))
                }else if (checkLb){
                    LB.setBackgroundResource(R.drawable.bg_unit)
                    ST.setBackgroundResource(R.color.transfer)
                    KG.setBackgroundResource(R.color.transfer)
                    rulerWeight.initViewParam(height!!, 2.2f, 663f, 0.1f)
                    rulerWeight.setUnit(getString(R.string.lb))
                }else{
                    ST.setBackgroundResource(R.drawable.bg_unit)
                    KG.setBackgroundResource(R.color.transfer)
                    LB.setBackgroundResource(R.color.transfer)
                    rulerWeight.initViewParam(height!!, 0f, 48f, 0.1f)
                    rulerWeight.setUnit(getString(R.string.st))
                }
                actionOnTextChange()
                viewGender(gender!!)
            }
        }

    }

    override fun initView() {
        Admob.getInstance().loadBannerFragment(this, getString(R.string.banner_all), binding.includeBanner)
    }

    override fun initListener() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
            btnCancel.setOnClickListener {
                finish()
            }
            btnSave.setOnClickListener {
                if (isClick){
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
            icCalender.setOnClickListener {
                if (isClick){
                    isClick = false
                    dialogDate.show()
                    handler.postDelayed({ isClick = true},500)
                }
            }
        }
    }
    private fun actionChooseGender(s: String, rb: AppCompatRadioButton, tv: TextView) {
        gender = s
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
    private fun getAvatar(avt: Int) {
        val list: List<Int> = initAvatarList(this)
        for (i in list.indices) {
            if (avt == list[i]) {
                sharePre.putInt("selectedItem", i)
            }
        }
        binding.imgAvt.setImageResource(avt)
        binding.imgAvt.setOnClickListener {
            dialogAvt = DialogAvt(this,object : OnSaveListener{
                override fun onClickSave(avt: Int) {
                    for (i in list.indices) {
                        if (avt == list[i]) sharePre.putInt("selectedItem", i)
                    }
                    binding.imgAvt.setImageResource(avt)
                    this@EditInformationActivity.avt = avt
                }
            })
            dialogAvt.show()
        }
    }

    private fun viewGender(gender: String) {
        when (gender) {
            MALE -> {
                binding.rbMale.setChecked(true)
                binding.txtMale.setTypeface(null, Typeface.BOLD)
            }

            FEMALE -> {
                binding.rbFemale.setChecked(true)
                binding.txtFemale.setTypeface(null, Typeface.BOLD)
            }

            OTHER -> {
                binding.rbOther.setChecked(true)
                binding.txtOther.setTypeface(null, Typeface.BOLD)
            }
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
                        "gender" to (gender ?: ""),
                        "checkCm" to checkCm,
                        "checkSt" to checkSt,
                        "checkLb" to checkLb,
                        "checkKg" to checkKg,
                    )

                    RealtimeDAO.updateRealtimeData(
                        myCode,
                        users,
                        RealtimeDAO.onSuccessListener {
                            isClick = true
                            Admob.getInstance().setOpenActivityAfterShowInterAds(true)
                            finish()
                        })
                }
            })
        }
    }

}