package com.kan.dev.familyhealth.ui.activity.BMI

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.adapter.BMIInformationAdapter
import com.kan.dev.familyhealth.adapter.IBMIRecentListener
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.RealtimeDAO
import com.kan.dev.familyhealth.data.RealtimeDAO.getRealtimeData
import com.kan.dev.familyhealth.data.model.BMI
import com.kan.dev.familyhealth.databinding.ActivityBmiinformationBinding
import com.kan.dev.familyhealth.utils.BMIS
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick
import com.lvt.ads.callback.InterCallback
import com.lvt.ads.util.Admob

class BMIInformationActivity : BaseActivity<ActivityBmiinformationBinding>(), IBMIRecentListener {
    override fun setViewBinding(): ActivityBmiinformationBinding {
        return ActivityBmiinformationBinding.inflate(layoutInflater)
    }
    private val adapter : BMIInformationAdapter by lazy {
        BMIInformationAdapter(this,this)
    }
    private var listBMI : ArrayList<BMI> = ArrayList()
    private lateinit var code :String
    override fun initData() {
        RealtimeDAO.initRealtimeData()
        code = intent.getStringExtra("CODE")!!

        getRealtimeData("$code/BMI",
            RealtimeDAO.valueEventListener { snapshot ->
                for (dataSnapshot in snapshot!!.children) {
                    val model = dataSnapshot.getValue(BMI::class.java)
                    model?.let {
                        listBMI.add(it)
                    }
                }

                if (listBMI.isNotEmpty()) {
                    binding.layoutNoData.root.visibility = View.INVISIBLE
                } else {
                    binding.layoutNoData.root.visibility = View.VISIBLE
                }
                adapter.setItems(listBMI)
                binding.rcvRecent.adapter = adapter
                binding.rcvRecent.layoutManager =
                    LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            })
    }

    override fun initView() {
        Admob.getInstance().loadCollapsibleBanner(this,getString(R.string.banner_collap))
    }

    override fun initListener() {
        binding.apply {
            icBack.setOnClickListener {
                finish()
            }
        }
        
    }

    override fun clickRecent(item: BMI) {
        if (isClick) {
            isClick = false
            Admob.getInstance().showInterAll(this, object : InterCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    item.isRecent = true
                    startActivity(Intent(this@BMIInformationActivity, CalculatorBMIActivity::class.java).putExtra("BMIInformation", item))
                }
            })
            handler.postDelayed(Runnable { isClick = true }, 500)
        }
    }
}