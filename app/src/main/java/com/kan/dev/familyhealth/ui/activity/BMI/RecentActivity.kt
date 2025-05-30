package com.kan.dev.familyhealth.ui.activity.BMI

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.adapter.RecentAdapter
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.RealtimeDAO
import com.kan.dev.familyhealth.data.model.BMI
import com.kan.dev.familyhealth.databinding.ActivityRecentBinding
import com.kan.dev.familyhealth.interfacces.IRecentListener
import com.kan.dev.familyhealth.utils.BMIS
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick
import com.kan.dev.familyhealth.viewmodel.BMIViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecentActivity : BaseActivity<ActivityRecentBinding>(),IRecentListener {
    override fun setViewBinding(): ActivityRecentBinding {
        return ActivityRecentBinding.inflate(layoutInflater)
    }
    private val viewModel :BMIViewModel by viewModels()
    private val adapter : RecentAdapter by lazy {
        RecentAdapter(this,this)
    }
    private val myCode by lazy {
        sharePre.getString(MY_CODE,"")
    }
    override fun initData() {
        viewModel.getAll.observe(this){
            if (it.isNotEmpty()) {
                binding.layoutNoData.root.visibility = View.INVISIBLE
            } else {
                binding.layoutNoData.root.visibility = View.VISIBLE
            }
            adapter.setItems(it)
            binding.rcvRecent.adapter = adapter
            binding.rcvRecent.layoutManager =
                LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
    }

    override fun initView() {
    }

    override fun initListener() {
        binding.icBack.setOnClickListener { finish() }
    }

    override fun deleteRecent(bmi: BMI) {
        RealtimeDAO.removeRealtimeData(myCode + "/BMI/" + bmi.id){
            Toast.makeText(this,getString(R.string.DeletedSuccessfully), Toast.LENGTH_SHORT).show()
            viewModel.delete(bmi)
        }
    }

    override fun clickRecent(item: BMI) {
        if (isClick) {
            isClick = false
            item.isRecent = true
            intent = Intent(this@RecentActivity, CalculatorBMIActivity::class.java)
            intent!!.putExtra(BMIS, item)
            startActivity(intent)
            handler.postDelayed(Runnable { isClick = true }, 500)
        }
    }
}