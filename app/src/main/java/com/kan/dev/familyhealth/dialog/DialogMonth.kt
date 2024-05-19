package com.kan.dev.familyhealth.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.kan.dev.familyhealth.adapter.FilterAdapter
import com.kan.dev.familyhealth.adapter.FilterYearAdapter
import com.kan.dev.familyhealth.adapter.IFilterItem
import com.kan.dev.familyhealth.adapter.IFilterYearItem
import com.kan.dev.familyhealth.data.Data.Companion.filterList
import com.kan.dev.familyhealth.data.Data.Companion.yearList
import com.kan.dev.familyhealth.databinding.DialogFilterBinding
import com.kan.dev.familyhealth.interfacces.IMonthClickListener
import com.kan.dev.familyhealth.utils.currentMonth
import com.kan.dev.familyhealth.utils.currentYear
import com.kan.dev.familyhealth.utils.getCurrentMonth
import com.kan.dev.familyhealth.utils.monthly

@SuppressLint("NewApi")
class DialogMonth(private val context: Context, val listener: IMonthClickListener): Dialog(context),
    IFilterItem, IFilterYearItem {

    private val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    private val binding by lazy {
        DialogFilterBinding.inflate(layoutInflater)
    }

    private val adapter  by lazy {
        FilterAdapter(context,this)
    }
    private val adapterYear by lazy {
        FilterYearAdapter(this)
    }
    private lateinit var monthItem : String
    private var index : Int = currentMonth
    private var year : Int = currentYear

    @SuppressLint("RestrictedApi", "RtlHardcoded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.decorView?.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
        val layoutParams = window?.attributes
        layoutParams?.x = dpToPx(context,30f)
        layoutParams?.y = dpToPx(context,180f)
        window?.attributes?.gravity = Gravity.TOP or Gravity.RIGHT
        window?.attributes = layoutParams
        binding.rcvFilter.layoutManager = layoutManager

        monthItem = getCurrentMonth(context)
        adapter.setItems(filterList)
        adapterYear.setItems(yearList)

        if (monthly){
            binding.rcvFilter.adapter = adapterYear
            adapterYear.setCheck(currentYear)
        }else{
            binding.rcvFilter.adapter = adapter
            adapter.setCheck(monthItem)
        }
    }

    fun dpToPx(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    override fun show() {
        super.show()
        binding.tvOk.setOnClickListener{
            listener.onClickListener(monthItem,index)
            listener.onClickYearListener(year)
            dismiss()
        }
        binding.tvCancel.setOnClickListener{
            dismiss()
        }
    }

    override fun dismiss() {
        super.dismiss()
        listener.onHideSystemBar()
    }
    override fun onClickItemFilter(month: String, index: Int) {
        if (!monthly){
            monthItem = month
            this.index = index
        }
    }

    override fun onClickItemFilter(year: Int) {
        if (monthly){
           this.year = year
        }
    }


}