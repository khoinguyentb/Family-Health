package com.kan.dev.familyhealth.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.helper.widget.MotionEffect
import androidx.core.content.ContextCompat
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseAdapter
import com.kan.dev.familyhealth.data.RealtimeDAO.getRealtimeData
import com.kan.dev.familyhealth.data.model.FriendModel
import com.kan.dev.familyhealth.databinding.ItemFriendBinding
import com.kan.dev.familyhealth.ui.activity.main.FriendActivity
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.SharePreferencesUtils
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick
import com.lvt.ads.callback.InterCallback
import com.lvt.ads.util.Admob

class FriendAdapter(
    private val context: Context,
    private val type: String,
    private val listener: DetailListener
) : BaseAdapter<FriendModel, ItemFriendBinding>() {
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemFriendBinding {
        return ItemFriendBinding.inflate(inflater, parent, false)
    }

    private lateinit var sharePre: SharePreferencesUtils
    private lateinit var myCode: String
    private var batterys: Int = 0
    private var avt: Int = 0
    private var name : String = ""
    private var nickname = ""
    private var isTracking = true
    private var online = true
    private var visible = true
    @SuppressLint("SetTextI18n")
    override fun bind(binding: ItemFriendBinding, item: FriendModel, position: Int) {

        sharePre = SharePreferencesUtils(context)
        myCode = sharePre.getString(MY_CODE, "")!!
        binding.apply {
            if (position == 0) {
                if (type == "online") {
                    imgFriendAvt.setImageResource(R.drawable.ic_add_friend)
                } else {
                    imgFriendAvt.setImageResource(R.drawable.ic_add_friend1)
                }
                layoutDetail.visibility = View.GONE
                imgOnline.visibility = View.GONE
                txtFriendName.text = context.getString(R.string.tv_ADD)
                battery.visibility = View.INVISIBLE
                imgFriendAvt.backgroundTintList = ColorStateList.valueOf(
                    if (type == "online") context.getColor(
                        R.color.white
                    ) else Color.parseColor("#FFA83BFD")
                )
                root.setOnClickListener {
                    Admob.getInstance().showInterAll(context, object : InterCallback() {
                        override fun onNextAction() {
                            super.onNextAction()
                            context.startActivity(Intent(context, FriendActivity::class.java))
                        }
                    })
                }
            } else if (position == 1) {
                if (type == "main" || type == "online") {
                    getRealtimeData(myCode) { snapshot ->
                        batterys = snapshot!!.child("battery").getValue(Int::class.java)!!
                        avt = snapshot.child("avt").getValue(Int::class.java)!!
                        imgFriendAvt.setImageResource(avt)
                        txtFriendName.text = context.getString(R.string.tv_me)
                        txtBattery.text = "$batterys%"
                        when (batterys) {
                            in 1..20 -> {
                                icPin.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.fa_batteryic_pin_1
                                    )
                                )
                            }

                            in 21..34 -> {
                                icPin.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.fa_batteryic_pin_2
                                    )
                                )
                            }

                            in 35..59 -> {
                                icPin.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.fa_batteryic_pin_3
                                    )
                                )
                            }

                            in 61..84 -> {
                                icPin.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.fa_batteryic_pin_4
                                    )
                                )
                            }

                            else -> {
                                icPin.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.fa_batteryic_pin_5
                                    )
                                )
                            }
                        }
                    }
                    if (type == "online") {
                        layoutDetail.visibility = View.GONE
                        root
                            .setOnClickListener { listener.onDetail(item.code,type) }
                        Log.e(MotionEffect.TAG, "getFriend: " + "online")
                    } else {
                        layoutDetail.visibility = View.VISIBLE
                        bg.visibility = View.GONE
                        btnDetail.setOnClickListener {
                            if (isClick) {
                                isClick = false
                                listener.onDetail(item.code,type)
                                handler.postDelayed({ isClick = true }, 500)
                            }
                        }
                    }
                } else {
                    getRealtimeData(myCode + "/friends/" + item.code) { snapshot ->
                        try {
                            name = snapshot!!.child("name").getValue(String::class.java)!!
                            avt = snapshot.child("avt").getValue(Int::class.java)!!
                            imgFriendAvt.setImageResource(avt)
                            nickname = snapshot.child("nickname").getValue(String::class.java)!!
                            isTracking = snapshot.child("isTracking").getValue(Boolean::class.java)!!
                            online = snapshot.child("online").getValue(Boolean::class.java)!!
                            if (nickname.trim { it <= ' ' } == "") txtFriendName.text = name else txtFriendName.text = nickname
                            batterys = snapshot.child("battery").getValue(Int::class.java)!!
                            txtBattery.text = "$batterys%"
                            visible = snapshot.child("visible").getValue(Boolean::class.java)!!
                            when (batterys) {
                                in 1..20 -> {
                                    icPin.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            context,
                                            R.drawable.fa_batteryic_pin_1
                                        )
                                    )
                                }
                                in 21..34 -> {
                                    icPin.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            context,
                                            R.drawable.fa_batteryic_pin_2
                                        )
                                    )
                                }
                                in 35..59 -> {
                                    icPin.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            context,
                                            R.drawable.fa_batteryic_pin_3
                                        )
                                    )
                                }
                                in 61..84 -> {
                                    icPin.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            context,
                                            R.drawable.fa_batteryic_pin_4
                                        )
                                    )
                                }
                                else -> {
                                    icPin.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            context,
                                            R.drawable.fa_batteryic_pin_5
                                        )
                                    )
                                }
                            }
                            if (isTracking && online && visible) {
                                imgOnline.visibility = View.VISIBLE
                                battery.visibility = View.VISIBLE
                                btnDetail.visibility = View.VISIBLE
                            } else {
                                imgOnline.visibility = View.GONE
                                battery.visibility = View.INVISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    layoutDetail.visibility = View.VISIBLE
                    bg.visibility = View.GONE
                    btnDetail.setOnClickListener {
                        if (isClick) {
                            isClick = false
                            listener.onDetail(item.code,type)
                            handler.postDelayed({ isClick = true }, 500)
                        }
                    }
                    Log.e(MotionEffect.TAG, "getFriend: " + "visible")
                }
            } else {
                getRealtimeData(myCode + "/friends/" + item.code) { snapshot ->
                    try {
                        avt = snapshot!!.child("avt").getValue(Int::class.java)!!
                        imgFriendAvt.setImageResource(avt)
                        name = snapshot.child("name").getValue(String::class.java)!!
                        nickname =
                            snapshot.child("nickname").getValue(String::class.java)!!
                        isTracking =
                            snapshot.child("isTracking").getValue(Boolean::class.java)!!
                        online =
                            snapshot.child("online").getValue(Boolean::class.java)!!
                        if (nickname.trim { it <= ' ' } == "") txtFriendName.text = name else txtFriendName.text = nickname
                        batterys = snapshot.child("battery").getValue(Int::class.java)!!
                        visible = snapshot.child("visible").getValue(Boolean::class.java)!!
                        txtBattery.text = "$battery%"
                        when (batterys) {
                            in 1..20 -> {
                                icPin.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.fa_batteryic_pin_1
                                    )
                                )
                            }
                            in 21..34 -> {
                                icPin.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.fa_batteryic_pin_2
                                    )
                                )
                            }
                            in 35..59 -> {
                                icPin.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.fa_batteryic_pin_3
                                    )
                                )
                            }
                            in 61..84 -> {
                                icPin.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.fa_batteryic_pin_4
                                    )
                                )
                            }
                            else -> {
                                icPin.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.fa_batteryic_pin_5
                                    )
                                )
                            }
                        }
                        if (isTracking && online && visible) {
                            imgOnline.visibility = View.VISIBLE
                            battery.visibility = View.VISIBLE
                        } else {
                            imgOnline.visibility = View.GONE
                            battery.visibility = View.INVISIBLE
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
                if (type == "online") {
                    layoutDetail.visibility = View.GONE
                    root
                        .setOnClickListener { listener.onDetail(item.code,type) }
                    Log.e(MotionEffect.TAG, "getFriend: " + "online")
                } else {
                    layoutDetail.visibility = View.VISIBLE
                    bg.visibility = View.GONE
                    btnDetail.setOnClickListener {
                        if (isClick) {
                            isClick = false
                            listener.onDetail(item.code,type)
                            handler.postDelayed({ isClick = true }, 500)
                        }
                    }
                    Log.e(MotionEffect.TAG, "getFriend: " + "visible")
                }
            }
        }
    }
}

    interface DetailListener {
        fun onDetail(code: String,type: String)
    }