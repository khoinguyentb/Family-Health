package com.kan.dev.familyhealth.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseAdapter
import com.kan.dev.familyhealth.data.model.LanguageModel
import com.kan.dev.familyhealth.databinding.ItemLanguageBinding

class LanguageAdapter(private val context: Context,private val listener : ILanguageItem) : BaseAdapter<LanguageModel, ItemLanguageBinding>() {
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemLanguageBinding {
        return ItemLanguageBinding.inflate(inflater,parent,false)
    }

    override fun bind(binding: ItemLanguageBinding, item: LanguageModel, position: Int) {
        binding.apply {
            imgflag.setImageResource(item.img)
            tvLanguge.text = item.language
            if (item.active){
                root.setBackgroundResource(R.drawable.bg_language_selector)
            }else{
                root.setBackgroundResource(R.drawable.bg_language_unselector)
            }
            root.setOnClickListener{
                setCheck(item.code)
                listener.onClickItemLanguage(item.code)
            }
        }
    }
    fun setCheck(code: String?) {
        for (item in listItem) {
            item.active = item.code.equals(code)
        }
        notifyDataSetChanged()
    }
    interface ILanguageItem {
        fun onClickItemLanguage(code: String)
    }
}