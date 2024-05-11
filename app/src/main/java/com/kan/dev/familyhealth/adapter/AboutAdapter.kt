package com.kan.dev.familyhealth.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kan.dev.familyhealth.base.BaseAdapter
import com.kan.dev.familyhealth.data.model.AboutModel
import com.kan.dev.familyhealth.databinding.ItemAboutBinding


class AboutAdapter(private val context: Context) : BaseAdapter<AboutModel, ItemAboutBinding>() {
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemAboutBinding {
        return ItemAboutBinding.inflate(inflater,parent,false)
    }

    override fun bind(binding: ItemAboutBinding, item: AboutModel, position: Int) {
        binding.cardView.setCardBackgroundColor(context.getColor(item.colors))
        binding.tvTitle.setText(item.text)
        binding.tvTitle.setSelected(true)
        binding.tvDes.setText(item.des)
        binding.tvDes.setTextColor(context.getColor(item.colors))
    }

}
