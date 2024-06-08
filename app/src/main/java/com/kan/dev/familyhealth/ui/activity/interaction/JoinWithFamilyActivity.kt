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
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.RealtimeDAO
import com.kan.dev.familyhealth.data.RealtimeDAO.pushRealtimeData
import com.kan.dev.familyhealth.data.model.FriendModel
import com.kan.dev.familyhealth.databinding.ActivityJoinWithFamilyBinding
import com.kan.dev.familyhealth.ui.activity.main.MainActivity
import com.kan.dev.familyhealth.ui.activity.QRScanActivity
import com.kan.dev.familyhealth.utils.CODE_LENGTH
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.QR_REQUEST_CODE
import com.kan.dev.familyhealth.utils.isClick
import com.kan.dev.familyhealth.utils.toastDuration
import com.kan.dev.familyhealth.viewmodel.FriendViewModel
import com.lvt.ads.callback.NativeCallback
import com.lvt.ads.util.Admob
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinWithFamilyActivity : BaseActivity<ActivityJoinWithFamilyBinding>() {
    override fun setViewBinding(): ActivityJoinWithFamilyBinding {
        return ActivityJoinWithFamilyBinding.inflate(layoutInflater)
    }
    private var friendCode: String? = null
    private var lastTime: Long = 0
    private val viewModel : FriendViewModel by viewModels()
    private var checkAdd = true
    private var database: DatabaseReference? = null

    private  var code = ""
    private  var avtId = 0
    private  var name = ""
    private  var gender = ""
    private  var weight = 0F
    private  var height = 0F
    private  var battery = 0
    private var dateOfBirth : String = ""
    private  var phoneNumber = ""
    private  var latLng = ""
    private  var lastActive = 0L
    private  var isTracking = true
    private  var isSos = false
    private var checkCm: Boolean = false
    private var checkSt: Boolean = false
    private var checkKg: Boolean = false
    private var checkLb: Boolean = false
    private lateinit var friend : FriendModel
    override fun initData() {
        database = FirebaseDatabase.getInstance().reference
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
            viewModel.getAll.observe(this){friends ->
                friends.forEach { friendModel ->
                    if (friendModel.code == friendCode){
                        checkAdd = false
                        return@observe
                    }
                }
            }
            if (friendCode == sharePre.getString(MY_CODE,"")){
                checkAdd = false
            }
            if (checkAdd){
                database?.child(friendCode!!)?.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            code = friendCode!!
                            avtId = snapshot.child("avt").getValue(Int::class.java) ?: 0
                            name = snapshot.child("name").getValue(String::class.java) ?: ""
                            gender = snapshot.child("gender").getValue(String::class.java) ?: ""
                            weight = snapshot.child("weight").getValue(Float::class.java) ?: 0F
                            height = snapshot.child("height").getValue(Float::class.java) ?: 0F
                            battery = snapshot.child("battery").getValue(Int::class.java) ?: 0
                            dateOfBirth = snapshot.child("dateOfBirth").getValue(String::class.java) ?: ""
                            phoneNumber = snapshot.child("phoneNumber").getValue(String::class.java) ?: ""
                            latLng = snapshot.child("latLng").getValue(String::class.java) ?: ""
                            lastActive = snapshot.child("lastActive").getValue(Long::class.java) ?: 0L
                            isTracking = snapshot.child("isTracking").getValue(Boolean::class.java) ?: false
                            isSos = snapshot.child("isSos").getValue(Boolean::class.java) ?: false

                            checkCm = snapshot.child("checkCm").getValue(Boolean::class.java) ?: false
                            checkLb = snapshot.child("checkLb").getValue(Boolean::class.java) ?: false
                            checkKg = snapshot.child("checkKg").getValue(Boolean::class.java) ?: false
                            checkSt = snapshot.child("checkSt").getValue(Boolean::class.java) ?: false

                            friend = FriendModel(code = code, avt = avtId, battery = battery, name = name, weight = weight,
                                height = height, dateOfBirth = dateOfBirth, nickname = "", gender = gender, phoneNumber = phoneNumber, latLng = latLng,
                                visible = true, isSos = isSos, statusSos = true, isTracking = isTracking, lastActive = lastActive, online = true, checkCm = checkCm,
                                checkKg = checkKg, checkLb = checkLb, checkSt = checkSt)

                            val users = hashMapOf<String, Any>(
                                "id" to 0,
                                "code" to code,
                                "avt" to avtId,
                                "battery" to battery,
                                "name" to name,
                                "nickname" to "",
                                "gender" to gender,
                                "phoneNumber" to phoneNumber,
                                "latLng" to latLng,
                                "visible" to true,
                                "isSos" to isSos,
                                "statusSos" to true,
                                "isTracking" to isTracking,
                                "lastActive" to lastActive,
                                "dateOfBirth" to dateOfBirth,
                                "online" to true,
                                "checkCm" to checkCm,
                                "checkSt" to checkSt,
                                "checkLb" to checkLb,
                                "checkKg" to checkKg,
                                "weight" to weight,
                                "height" to height,
                            )
                            if (snapshot.key != sharePre.getString(MY_CODE,"")) {
                                pushRealtimeData("${sharePre.getString(MY_CODE,"")}/friends/${users["code"]}", users) {
                                    viewModel.insert(friend)
                                    if (System.currentTimeMillis() - lastTime > toastDuration) {
                                        Toast.makeText(
                                            applicationContext,
                                            getString(R.string.join_with_friend),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        lastTime = System.currentTimeMillis()
                                        binding.edtCode.setText("")
                                        isClick = true
                                    }
                                }
                            }else{
                                if (System.currentTimeMillis() - lastTime > toastDuration) {
                                    Toast.makeText(
                                        applicationContext,
                                        getString(R.string.join_with_friend),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    lastTime = System.currentTimeMillis()
                                    binding.edtCode.setText("")
                                    isClick = true
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }else{
                Toast.makeText(this,getString(R.string.InvalidCode),Toast.LENGTH_SHORT).show()
            }
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