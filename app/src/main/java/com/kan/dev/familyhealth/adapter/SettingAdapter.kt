package com.kan.dev.familyhealth.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.kan.dev.familyhealth.base.BaseAdapter
import com.kan.dev.familyhealth.data.model.SettingModel
import com.kan.dev.familyhealth.databinding.ItemSettingBinding
import com.kan.dev.familyhealth.interfacces.ISettingClickListener

class SettingAdapter(private val listener: ISettingClickListener) :BaseAdapter<SettingModel,ItemSettingBinding>() {
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemSettingBinding {
        return ItemSettingBinding.inflate(inflater,parent,false)
    }

    override fun bind(binding: ItemSettingBinding, item: SettingModel, position: Int) {
       binding.apply {
           icSetting.setImageResource(item.image)
           tvItemSetting.setText(item.text)
           root.setOnClickListener {
               listener.clickItemSetting(item.image)
           }
       }
    }
}