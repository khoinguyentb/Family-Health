package com.kan.dev.familyhealth.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseAdapter
import com.kan.dev.familyhealth.data.model.FilterModel
import com.kan.dev.familyhealth.databinding.ItemFilterBinding

class FilterAdapter(private val context: Context,private val listener : IFilterItem) :BaseAdapter<FilterModel,ItemFilterBinding>() {
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemFilterBinding {
        return ItemFilterBinding.inflate(inflater,parent,false)
    }

    override fun bind(binding: ItemFilterBinding, item: FilterModel, position: Int) {
        if (item.active) {
            binding.imgSelect.setImageResource(R.drawable.icon_selector)
        } else {
            binding.imgSelect.setImageResource(R.drawable.icon_unselector)
        }
        binding.tvMonth.setText(item.month)
        binding.root.setOnClickListener {
            listener.onClickItemFilter(context.getString(item.month), item.monthIndex)
            setCheck(context.getString(item.month))
        }
    }

    fun setCheck(month: String) {
        for (item in listItem) {
            item.active = context.getString(item.month).equals(month)
        }
        notifyDataSetChanged()
    }
}

interface IFilterItem {
    fun onClickItemFilter(month: String, index: Int)
}
