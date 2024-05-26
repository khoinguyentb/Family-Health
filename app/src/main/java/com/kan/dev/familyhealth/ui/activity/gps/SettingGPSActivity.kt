package com.kan.dev.familyhealth.ui.activity.gps

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.adapter.DetailListener
import com.kan.dev.familyhealth.adapter.FriendAdapter
import com.kan.dev.familyhealth.adapter.PlaceAdapter
import com.kan.dev.familyhealth.adapter.PlaceListener
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.Data.Companion.placeList
import com.kan.dev.familyhealth.data.RealtimeDAO.getRealtimeData
import com.kan.dev.familyhealth.data.RealtimeDAO.initRealtimeData
import com.kan.dev.familyhealth.data.RealtimeDAO.updateRealtimeData
import com.kan.dev.familyhealth.data.model.FriendModel
import com.kan.dev.familyhealth.databinding.ActivitySettingGpsactivityBinding
import com.kan.dev.familyhealth.ui.activity.EditInformationActivity
import com.kan.dev.familyhealth.ui.activity.interaction.ShareInformationActivity
import com.kan.dev.familyhealth.ui.activity.main.DetailInformationActivity
import com.kan.dev.familyhealth.ui.activity.main.FriendActivity
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick
import com.kan.dev.familyhealth.viewmodel.FriendViewModel
import com.lvt.ads.callback.InterCallback
import com.lvt.ads.util.Admob
import com.lvt.ads.util.AppOpenManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingGPSActivity : BaseActivity<ActivitySettingGpsactivityBinding>() {
    override fun setViewBinding(): ActivitySettingGpsactivityBinding {
        return ActivitySettingGpsactivityBinding.inflate(layoutInflater)
    }

    private val viewModel: FriendViewModel by viewModels()
    private lateinit var myCode: String
    private var isTracking: Boolean = true

    private lateinit var placeAdapter: PlaceAdapter
    private lateinit var friendAdapter: FriendAdapter

    private val friendDefault: FriendModel by lazy {
        FriendModel(
            "00000000", 0, 0, "name", 0f, 0f, "", "", "", "",
            checkCm = true,
            checkSt = true,
            checkKg = true,
            checkLb = true,
            latLng = "",
            visible = true,
            isSos = true,
            statusSos = true,
            isTracking = true,
            lastActive = 0L,
            online = true
        )
    }

    override fun initData() {
        initRealtimeData()
        myCode = sharePre.getString(MY_CODE, "")!!
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        Admob.getInstance().loadCollapsibleBanner(this, getString(R.string.banner_collap), "bottom")
        binding.apply {
            btnQR.isSelected = true

            getRealtimeData(myCode) { snapshot ->
                val avt: Int = snapshot!!.child("avt").getValue(Int::class.java)!!
                val battery: Int = snapshot.child("battery").getValue(Int::class.java)!!
                val phoneNumber: String =
                    snapshot.child("phoneNumber").getValue(String::class.java)!!
                isTracking = snapshot.child("isTracking").getValue(Boolean::class.java)!!
                txtPhoneNum.text = getString(R.string.phone_number) + " " + phoneNumber
                imgFriendAvt.setImageResource(avt)
                btnActive.setImageResource(if (isTracking) R.drawable.switch_on else R.drawable.switch_off)
                txtBattery.text = "$battery%"
                when (battery) {
                    in 1..20 -> {
                        binding.txtBattery.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            ContextCompat.getDrawable(
                                this@SettingGPSActivity, R.drawable.fa_batteryic_pin_1
                            ), null, null, null
                        )
                    }

                    in 21..34 -> {
                        binding.txtBattery.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            ContextCompat.getDrawable(
                                this@SettingGPSActivity, R.drawable.fa_batteryic_pin_2
                            ), null, null, null
                        )
                    }

                    in 35..59 -> {
                        binding.txtBattery.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            ContextCompat.getDrawable(
                                this@SettingGPSActivity, R.drawable.fa_batteryic_pin_3
                            ), null, null, null
                        )
                    }

                    in 61..84 -> {
                        binding.txtBattery.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            ContextCompat.getDrawable(
                                this@SettingGPSActivity, R.drawable.fa_batteryic_pin_4
                            ), null, null, null
                        )
                    }

                    else -> {
                        binding.txtBattery.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            ContextCompat.getDrawable(
                                this@SettingGPSActivity, R.drawable.fa_batteryic_pin_5
                            ), null, null, null
                        )
                    }
                }
            }

        }
    }

    override fun initListener() {
        binding.apply {
            btnActive.setOnClickListener { actionActiveTracking() }
            btnBack.setOnClickListener { onBackPressed() }
            btnViewAddFriend.setOnClickListener {
                if (isClick) {
                    isClick = false
                    Admob.getInstance()
                        .showInterAll(this@SettingGPSActivity, object : InterCallback() {
                            override fun onNextAction() {
                                super.onNextAction()
                                startActivity(
                                    Intent(
                                        this@SettingGPSActivity,
                                        FriendActivity::class.java
                                    )
                                )
                            }
                        })
                    handler.postDelayed({ isClick = true }, 500)
                }
            }

            btnViewPlace.setOnClickListener {
                if (isClick) {
                    isClick = false
                    Admob.getInstance()
                        .showInterAll(this@SettingGPSActivity, object : InterCallback() {
                            override fun onNextAction() {
                                super.onNextAction()
                                startActivity(
                                    Intent(
                                        this@SettingGPSActivity,
                                        PlaceActivity::class.java
                                    )
                                )
                            }
                        })
                    handler.postDelayed({ isClick = true }, 500)
                }
            }

            btnQR.setOnClickListener {
                if (isClick) {
                    isClick = false
                    startActivity(
                        Intent(
                            this@SettingGPSActivity,
                            ShareInformationActivity::class.java
                        ).putExtra("settings", true)
                    )
                    handler
                        .postDelayed({ isClick = true }, 500)
                }
            }
            btnEdit.setOnClickListener {
                if (isClick) {
                    isClick = false
                    startActivity(
                        Intent(
                            this@SettingGPSActivity,
                            EditInformationActivity::class.java
                        )
                    )
                    handler
                        .postDelayed({ isClick = true }, 500)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        AppOpenManager.getInstance().enableAppResumeWithActivity(SettingGPSActivity::class.java)
        getFriend()
        getPlace()
        binding.recyclerPlace.isNestedScrollingEnabled = false
    }

    private fun actionActiveTracking() {
        val tracking: MutableMap<String, Any> = HashMap()
        tracking["isTracking"] = !isTracking
        tracking["isSos"] = false
        updateRealtimeData(myCode, tracking) { _ -> }

        binding.btnActive.setImageResource(if (isTracking) R.drawable.switch_on else R.drawable.switch_off)
    }

    private fun getFriend() {

        viewModel.getAll.observe(this) {
            binding.apply {
                recyclerFriend.layoutManager = LinearLayoutManager(
                    this@SettingGPSActivity, LinearLayoutManager.HORIZONTAL, false
                )
                recyclerFriend.setHasFixedSize(true)
                friendAdapter = FriendAdapter(this@SettingGPSActivity, "online", object :
                    DetailListener {
                    override fun onDetail(code: String, type: String) {
                        val intent =
                            Intent(applicationContext, DetailInformationActivity::class.java)
                        intent.putExtra("receiveCode", code)
                        startActivity(intent)
                    }
                })
                friendAdapter.addItem(friendDefault, 0)
                friendAdapter.addItem(friendDefault, 0)
                recyclerFriend.adapter = friendAdapter
                recyclerFriend.itemAnimator = null
            }
        }
    }

    private fun getPlace() {
        binding.apply {
            recyclerPlace.layoutManager =
                LinearLayoutManager(this@SettingGPSActivity, LinearLayoutManager.VERTICAL, false)
            recyclerPlace.setHasFixedSize(true)
            placeAdapter = PlaceAdapter(this@SettingGPSActivity, object : PlaceListener {
                override fun onFind(type: String) {
                    if (isClick) {
                        isClick = false
                        val intent = Intent(this@SettingGPSActivity, GPSActivity::class.java)
                        intent.putExtra("place", type)
                        startActivity(intent)
                        finish()
                        handler.postDelayed({ isClick = true }, 500)
                    }
                }
            })
            placeAdapter.setItems(placeList.subList(0, 3))
            recyclerPlace.adapter = placeAdapter
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, GPSActivity::class.java))
        finish()
    }

}