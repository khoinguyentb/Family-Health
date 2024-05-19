package com.kan.dev.familyhealth.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseAdapter
import com.kan.dev.familyhealth.data.model.YearModel
import com.kan.dev.familyhealth.databinding.ItemFilterBinding

class FilterYearAdapter(private val listener : IFilterYearItem) :BaseAdapter<YearModel,ItemFilterBinding>() {
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemFilterBinding {
        return ItemFilterBinding.inflate(inflater,parent,false)
    }

    override fun bind(binding: ItemFilterBinding, item: YearModel, position: Int) {

        binding.apply {
            tvMonth.text = item.year.toString()
            if (item.active) {
                imgSelect.setImageResource(R.drawable.icon_selector)
            } else {
                imgSelect.setImageResource(R.drawable.icon_unselector)
            }
            root.setOnClickListener { v ->
                setCheck(item.year)
                listener.onClickItemFilter(item.year)
            }
        }
    }

    fun setCheck(year: Int) {
        for (item in listItem) {
            item.active = item.year == year
        }
        notifyDataSetChanged()
    }
}


interface IFilterYearItem {
    fun onClickItemFilter(year: Int)
}
