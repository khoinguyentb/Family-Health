package com.kan.dev.familyhealth.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseAdapter
import com.kan.dev.familyhealth.data.RealtimeDAO.getOnetimeData
import com.kan.dev.familyhealth.data.RealtimeDAO.removeRealtimeData
import com.kan.dev.familyhealth.data.RealtimeDAO.updateRealtimeData
import com.kan.dev.familyhealth.data.model.FriendModel
import com.kan.dev.familyhealth.databinding.ItemFriendDetailBinding
import com.kan.dev.familyhealth.dialog.DialogDeleteFriend
import com.kan.dev.familyhealth.dialog.DialogRename
import com.kan.dev.familyhealth.dialog.OnclickDeleteListener
import com.kan.dev.familyhealth.dialog.OnclickListener
import com.kan.dev.familyhealth.ui.activity.main.DetailInformationActivity
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.SharePreferencesUtils

class FriendDetailAdapter(private val context: Context,private val listener: DetailListeners) : BaseAdapter<FriendModel,ItemFriendDetailBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemFriendDetailBinding {
        return ItemFriendDetailBinding.inflate(inflater,parent,false)
    }

    private lateinit var sharePre : SharePreferencesUtils
    private lateinit var myCode: String
    private var avt = 0
    private var name = ""
    private var nickname = ""
    private var phoneNumber = ""
    private var online = true
    private var batterys = 0
    private var isTracking = true
    private var visible = true
    @SuppressLint("SetTextI18n")
    override fun bind(binding: ItemFriendDetailBinding, item: FriendModel, position: Int) {
        
        sharePre = SharePreferencesUtils(context)
        myCode = sharePre.getString(MY_CODE,"")!!

        binding.apply {
            getOnetimeData(myCode + "/friends/" + item.code) { snapshot ->
                avt= snapshot!!.child("avt").getValue(Int::class.java)!!
                name= snapshot.child("name").getValue(String::class.java)!!
                nickname= snapshot.child("nickname").getValue(String::class.java)!!
                phoneNumber = snapshot.child("phoneNumber").getValue(String::class.java)!!
                isTracking =snapshot.child("isTracking").getValue(Boolean::class.java)!!
                online = snapshot.child("online").getValue(Boolean::class.java)!!
                batterys = snapshot.child("battery").getValue(Int::class.java)!!
                visible = snapshot.child("visible").getValue(Boolean::class.java)!!
                txtPhoneNum.text = context.getString(R.string.phone_number) + " " + phoneNumber
                imgFriendAvt.setImageResource(avt)
                btnActive.setImageResource(if (visible) R.drawable.switch_on else R.drawable.switch_off)
                if (nickname.trim { it <= ' ' } == "") txtFriendName.setText(name) else txtFriendName.setText(
                    nickname
                )
                txtBattery.text = "$batterys%"
                when (batterys) {
                    in 1..20 -> {
                        txtBattery.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            ContextCompat.getDrawable(context, R.drawable.fa_batteryic_pin_1),
                            null, null, null
                        )
                    }
                    in 21..34 -> {
                        txtBattery.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            ContextCompat.getDrawable(context, R.drawable.fa_batteryic_pin_2),
                            null, null, null
                        )
                    }
                    in 35..59 -> {
                        txtBattery.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            ContextCompat.getDrawable(context, R.drawable.fa_batteryic_pin_3),
                            null, null, null
                        )
                    }
                    in 61..84 -> {
                        txtBattery.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            ContextCompat.getDrawable(context, R.drawable.fa_batteryic_pin_4),
                            null, null, null
                        )
                    }
                    else -> {
                        txtBattery.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            ContextCompat.getDrawable(context, R.drawable.fa_batteryic_pin_5),
                            null, null, null
                        )
                    }
                }
                if (isTracking && online && visible) {
                    imgOnline.setVisibility(View.VISIBLE)
                    txtBattery.setVisibility(View.VISIBLE)
                } else {
                    imgOnline.setVisibility(View.GONE)
                    txtBattery.setVisibility(View.GONE)
                }
            }
            root.setOnClickListener {
                val intent = Intent(context, DetailInformationActivity::class.java)
                intent.putExtra("receiveCode", item.code)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            btnActive.setOnClickListener {
                visible = !visible
                item.visible = visible
                try {
                    val friend: MutableMap<String, Any> =
                        HashMap()
                    friend["visible"] = visible
                    friend["isSos"] = false
                    friend["statusSos"] = false
                    updateRealtimeData(myCode + "/friends/" + item.code, friend) { _ ->
                        btnActive.setImageResource(if (visible) R.drawable.switch_on else R.drawable.switch_off)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                notifyItemChanged(position)
            }
            btnEdit.setOnClickListener {
                val dialogEdit = DialogRename(context as Activity, object : OnclickListener {
                    override fun onClickRename(nickName: String) {
                        val friend: MutableMap<String, Any> =
                            HashMap()
                        friend["nickname"] = nickName
                        updateRealtimeData(
                            myCode + "/friends/" + item.code,
                            friend
                        ) { _ ->
                            notifyItemChanged(
                                position
                            )
                        }
                    }
                }, txtFriendName.text.toString())
                dialogEdit.show()
            }
            btnDelete.setOnClickListener { view ->
                val dialogDelete =
                    DialogDeleteFriend(context as Activity, object : OnclickDeleteListener {
                        override fun onClickDelete() {
                            removeRealtimeData(myCode + "/friends/" + item.code) { unused ->
                                listener.onDelete(item)
                                notifyItemRemoved(position)
                            }
                        }
                    })
                dialogDelete.show()
            }
        }
    }
}

interface DetailListeners {
    fun onDelete(item : FriendModel)
}
