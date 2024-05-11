package com.kan.dev.familyhealth.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.adapter.AboutAdapter
import com.kan.dev.familyhealth.base.BaseFragment
import com.kan.dev.familyhealth.data.Data
import com.kan.dev.familyhealth.databinding.FragmentAboutBinding


class AboutFragment : BaseFragment<FragmentAboutBinding>() {
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAboutBinding {
        return FragmentAboutBinding.inflate(layoutInflater)
    }
    private lateinit var adapter: AboutAdapter
    override fun initData() {
        adapter = AboutAdapter(requireActivity())
        adapter.setItems(Data.aboutModelListAdults)
        binding.rcvAbout.setAdapter(adapter)
        binding.rcvAbout.setLayoutManager(
            LinearLayoutManager(
                requireActivity(),
                RecyclerView.VERTICAL,
                false
            )
        )
    }

    override fun initView() {
    }

    override fun initListener() {
        binding.Adults.setOnClickListener{
            setAbout(true)
        }
        binding.Teenagers.setOnClickListener{
            setAbout(false)
        }
    }

    private fun setAbout(check: Boolean) {
        binding.apply {
            if (check) {
                Adults.setBackgroundResource(R.drawable.bg_button_magenta)
                Teenagers.setBackgroundResource(R.color.transfer)
                adapter.setItems(Data.aboutModelListAdults)
                des1.setText(R.string.BMIForAdults)
                des2.setText(R.string.BMIForAdults1)
            } else {
                Teenagers.setBackgroundResource(R.drawable.bg_button_magenta)
                Adults.setBackgroundResource(R.color.transfer)
                adapter.setItems(Data.aboutModelListTeenagers)
                des1.setText(R.string.BMIForTeenagers)
                des2.setText(R.string.BMIForTeenagers1)
            }
        }
    }

}