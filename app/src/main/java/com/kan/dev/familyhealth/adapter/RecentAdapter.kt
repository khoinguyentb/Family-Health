package com.kan.dev.familyhealth.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseAdapter
import com.kan.dev.familyhealth.data.model.BMI
import com.kan.dev.familyhealth.databinding.ItemRecentBinding
import com.kan.dev.familyhealth.dialog.DialogDelete
import com.kan.dev.familyhealth.interfacces.IDeleteClickListener
import com.kan.dev.familyhealth.interfacces.IRecentListener
import com.kan.dev.familyhealth.utils.FEMALE

class RecentAdapter(private val context: Context,private val listener: IRecentListener) :BaseAdapter<BMI,ItemRecentBinding>(),IDeleteClickListener {
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemRecentBinding {
        return ItemRecentBinding.inflate(inflater,parent,false)
    }
    private lateinit var diaLog : DialogDelete
    private var gender: String? = null
    private var weight: String? = null
    private var height: String? = null
    private var intent: Intent? = null
    private lateinit var bmi: BMI
    override fun bind(binding: ItemRecentBinding, item: BMI, position: Int) {
        binding.apply {
            tvTime.text = item.time
            tvAge.text = item.age.toString()
            tvStatusBmi.isSelected = true
            tvGender.isSelected = true
            Age.isSelected = true
            Gender.isSelected = true
            Weight.isSelected = true
            Height.isSelected = true

            gender = if (item.gender.equals(FEMALE)) {
                context.getString(R.string.Female)
            } else {
                context.getString(R.string.Male)
            }

            height = if (item.checkCm) {
                item.height.toString() + context.getString(R.string.cm)
            } else {
                item.height.toString() + context.getString(R.string.ft)
            }

            weight = if (item.checkKg) {
                item.weight.toString() + context.getString(R.string.kg)
            } else if (item.checkLb) {
                item.weight.toString() + context.getString(R.string.lb)
            } else {
                item.weight.toString() + context.getString(R.string.st)
            }
            tvGender.text = gender
            tvWeight.text = weight
            tvHeight.text = height
            root.setOnClickListener {

                listener.clickRecent(item)
            }
            root.setOnLongClickListener {
                bmi = item
                diaLog = DialogDelete(context,this@RecentAdapter)
                diaLog.show()
                true
            }
            setStatusBMI(binding.tvStatusBmi, item.bmi, item.age)
        }
    }

    private fun setStatusBMI(tv: TextView, bmi: Float, age: Int) {
        if (age >= 21) {
            if (bmi < 16) {
                tv.text = context.getString(R.string.very_Severely_underweight)
                tv.setTextColor(context.getColor(R.color.very_severely_underweight))
            } else if (bmi >= 16 && bmi < 16.9) {
                tv.text = context.getString(R.string.severely_underweight)
                tv.setTextColor(context.getColor(R.color.severely_underweight))
            } else if (bmi >= 17 && bmi < 18.4) {
                tv.text = context.getString(R.string.underweight)
                tv.setTextColor(context.getColor(R.color.underweight))
            } else if (bmi >= 18.5 && bmi < 24.9) {
                tv.text = context.getString(R.string.normal)
                tv.setTextColor(context.getColor(R.color.normal))
            } else if (bmi >= 25 && bmi < 29.9) {
                tv.text = context.getString(R.string.overweight)
                tv.setTextColor(context.getColor(R.color.overweight))
            } else if (bmi >= 30 && bmi < 34.9) {
                tv.text = context.getString(R.string.obese_Class_I)
                tv.setTextColor(context.getColor(R.color.obese_class_I))
            } else if (bmi >= 35 && bmi < 39.9) {
                tv.text = context.getString(R.string.obese_Class_II)
                tv.setTextColor(context.getColor(R.color.obese_class_II))
            } else if (bmi > 40) {
                tv.text = context.getString(R.string.obese_class_III)
                tv.setTextColor(context.getColor(R.color.obese_class_III))
            }
        } else {
            if (bmi < 15.4) {
                tv.text = context.getString(R.string.underweight)
                tv.setTextColor(context.getColor(R.color.underweight20))
            } else if (bmi >= 15.4 && bmi < 22.5) {
                tv.text = context.getString(R.string.normal)
                tv.setTextColor(context.getColor(R.color.normal20))
            } else if (bmi >= 22.6 && bmi < 26.3) {
                tv.text = context.getString(R.string.overweight)
                tv.setTextColor(context.getColor(R.color.overweight20))
            } else if (bmi >= 26.4) {
                tv.text = context.getString(R.string.obese_Class_I)
                tv.setTextColor(context.getColor(R.color.obese_class_I20))
            }
        }
    }

    override fun clickDelete() {
        listener.deleteRecent(bmi)
    }

    override fun hideNavigation() {
    }
}