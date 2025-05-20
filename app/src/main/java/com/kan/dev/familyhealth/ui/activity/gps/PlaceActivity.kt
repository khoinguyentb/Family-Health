package com.kan.dev.familyhealth.ui.activity.gps

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.kan.dev.familyhealth.adapter.PlaceAdapter
import com.kan.dev.familyhealth.adapter.PlaceListener
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.Data.Companion.placeList
import com.kan.dev.familyhealth.databinding.ActivityPlaceBinding

class PlaceActivity : BaseActivity<ActivityPlaceBinding>() {
    override fun setViewBinding(): ActivityPlaceBinding {
        return ActivityPlaceBinding.inflate(layoutInflater)
    }
    private var check = false
    override fun initData() {
        
    }

    override fun initView() {

        val intent = intent
        if (intent != null) {
            check = intent.getBooleanExtra("main", false)
        }
        getPlace()
    }

    override fun initListener() {
        binding.imgBack.setOnClickListener {
            if (check) {
                startActivity(Intent(this, GPSActivity::class.java))
            }
            finish()
        }
    }

    private fun getPlace() {
        binding.recyclerPlace.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerPlace.setHasFixedSize(true)
        val adapter = PlaceAdapter(this,object : PlaceListener{
            override fun onFind(type: String) {
                val intent = Intent(applicationContext, GPSActivity::class.java)
                intent.putExtra("place", type)
                startActivity(intent)
            }
        })
        adapter.setItems(placeList)
        binding.recyclerPlace.adapter = adapter
        binding.recyclerPlace.itemAnimator = null
    }
}