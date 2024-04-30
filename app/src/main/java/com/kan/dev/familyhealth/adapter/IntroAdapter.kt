package com.kan.dev.familyhealth.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kan.dev.familyhealth.base.BaseAdapter
import com.kan.dev.familyhealth.data.model.IntroModel
import com.kan.dev.familyhealth.databinding.ItemIntroBinding

class IntroAdapter(private val context: Context) : BaseAdapter<IntroModel, ItemIntroBinding>() {
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemIntroBinding {
        return ItemIntroBinding.inflate(inflater,parent,false)
    }
    override fun bind(binding: ItemIntroBinding, item: IntroModel, position: Int) {
        binding.apply {
            imgIntro.setImageResource(item.img)
            titleIntro.text = context.getText(item.title)
            desIntro.text = context.getString(item.des)
        }
    }
}