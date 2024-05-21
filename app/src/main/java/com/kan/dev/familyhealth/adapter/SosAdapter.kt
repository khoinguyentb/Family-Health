package com.kan.dev.familyhealth.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseAdapter
import com.kan.dev.familyhealth.data.RealtimeDAO
import com.kan.dev.familyhealth.data.model.FriendModel
import com.kan.dev.familyhealth.databinding.DialogSosBinding
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.SharePreferencesUtils

class SosAdapter(private val context: Context,private val listener: OnclickStopListener) : BaseAdapter<FriendModel,DialogSosBinding>() {
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): DialogSosBinding {
        return DialogSosBinding.inflate(inflater,parent,false)
    }

    private lateinit var sharePre :SharePreferencesUtils
    private lateinit var myCode : String
    private lateinit var name :String
    private lateinit var nickName :String
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun bind(binding: DialogSosBinding, item: FriendModel, position: Int) {
        sharePre = SharePreferencesUtils(context)
        myCode = sharePre.getString(MY_CODE,"")!!
        binding.apply {
            RealtimeDAO.getOnetimeData(
                myCode + "/friends/" + item.code
            ) { snapshot ->
                try {
                    name = snapshot!!.child("name").getValue(String::class.java).toString()
                    nickName = snapshot.child("nickname").getValue(String::class.java).toString()
                    if (nickName.trim { it <= ' ' } == "") txtSosDesc.text = "$name " + context.getString(R.string.stop_sos)
                    else txtSosDesc.text = nickName + " " + context.getString(R.string.stop_sos)
                    btnActive.text = context.getString(R.string.turn_off)
                    btnActive.backgroundTintList = ColorStateList.valueOf(
                        context.getColor(
                            R.color.blue_4380F6
                        )
                    )
                    btnActive.setOnClickListener {
                        listener.onStop(item)
                        notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    notifyItemRemoved(position)
                    e.printStackTrace()
                }
            }
        }
    }
}


interface OnclickStopListener {
    fun onStop(friendModel: FriendModel)
}
