package com.kan.dev.familyhealth.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kan.dev.familyhealth.base.BaseAdapter
import com.kan.dev.familyhealth.data.model.PlaceModel
import com.kan.dev.familyhealth.databinding.ItemPlaceBinding

class PlaceAdapter(private val context: Context,private val listener: PlaceListener) : BaseAdapter<PlaceModel,ItemPlaceBinding>() {
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemPlaceBinding {
        return ItemPlaceBinding.inflate(inflater,parent,false)
    }

    override fun bind(binding: ItemPlaceBinding, item: PlaceModel, position: Int) {
        binding.apply {
            imgPlace.setImageResource(item.placeRes)
            txtPlace.setText(item.placeName)
            btnFind.setOnClickListener { listener.onFind(item.placeId) }
        }
    }
}

interface PlaceListener {
    fun onFind(type: String)
}
