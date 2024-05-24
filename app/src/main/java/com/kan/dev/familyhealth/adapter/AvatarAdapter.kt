package com.kan.dev.familyhealth.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseAdapter
import com.kan.dev.familyhealth.databinding.ItemAvatarBinding
import com.kan.dev.familyhealth.utils.SharePreferencesUtils

class AvatarAdapter(private val context: Context,private val listener : ClickAvt) :BaseAdapter<Int,ItemAvatarBinding>() {
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemAvatarBinding {
        return ItemAvatarBinding.inflate(inflater,parent,false)
    }
    private var selectedItem: Int = 0
    private lateinit var sharePre : SharePreferencesUtils
    @SuppressLint("NotifyDataSetChanged")
    override fun bind(binding: ItemAvatarBinding, item: Int, position: Int) {
        sharePre = SharePreferencesUtils(context)
        selectedItem = sharePre.getInt("selectedItem",0)
        binding.apply {
            Glide.with(context).load(item).dontAnimate().into(imgAvt)
            if (position == selectedItem) {
                selector.visibility = View.VISIBLE
            } else {
                selector.visibility = View.INVISIBLE
            }
            imgAvt.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.white))
            root.setOnClickListener {
                selectedItem = position
                listener.listener(item)
                notifyDataSetChanged()
            }
        }
    }
}

interface ClickAvt {
    fun listener(avt: Int)
}
