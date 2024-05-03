package com.kan.dev.familyhealth.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.kan.dev.familyhealth.databinding.DatePickerBottomBinding
import com.kan.dev.familyhealth.interfacces.IDateClickListener
import com.kan.dev.familyhealth.utils.SharePreferencesUtils
import com.kan.dev.familyhealth.utils.formatDate
import com.shawnlin.numberpicker.NumberPicker
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

class DialogDate (private val context: Context,private val listener : IDateClickListener) : Dialog(context) {
    private lateinit var sharePre : SharePreferencesUtils
    private val binding by lazy {
        DatePickerBottomBinding.inflate(layoutInflater)
    }
    private lateinit var date : String
    @SuppressLint("NewApi")
    private var currentMonth = LocalDate.now().monthValue
    @SuppressLint("NewApi")
    private var yearMonth = YearMonth.now()
    @SuppressLint("NewApi")
    private var currentMonthDays = yearMonth.lengthOfMonth()
    @SuppressLint("NewApi")
    private var currentYear = LocalDate.now().year
    @SuppressLint("NewApi")
    private var currentDate = LocalDate.now().dayOfMonth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.decorView?.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        window?.attributes?.gravity = Gravity.BOTTOM

        binding.npYear.formatter = object : NumberPicker.Formatter {
            override fun format(value: Int): String {
                return String.format(Locale.US, "%04d", value)
            }
        }
        binding.npMonths.formatter = object : NumberPicker.Formatter {
            override fun format(value: Int): String {
                return String.format(Locale.US, "%02d", value)
            }
        }

        binding.npDay.formatter = object : NumberPicker.Formatter {
            override fun format(value: Int): String {
                return String.format(Locale.US, "%02d", value)
            }
        }
        sharePre = SharePreferencesUtils(context)

        initNumberPickerYear()
        initNumberPickerYMonth()
        initNumberPickerDay()
    }

    private fun initNumberPickerYear(){
        binding.npYear.minValue = currentYear - 10
        binding.npYear.maxValue = currentYear + 10
        binding.npYear.value = currentYear

        binding.npYear.setOnValueChangedListener { picker, oldVal, newVal ->
            currentYear = newVal
            initNumberPickerDay()
        }
    }

    private fun initNumberPickerYMonth(){
        binding.npMonths.minValue = 1
        binding.npMonths.maxValue = 12
        binding.npMonths.value = currentMonth
        binding.npMonths.setOnValueChangedListener { picker, oldVal, newVal ->
            currentMonth = newVal
            initNumberPickerDay()
        }
    }

    @SuppressLint("NewApi")
    private fun initNumberPickerDay(){
        yearMonth = YearMonth.of(currentYear, currentMonth)
        currentMonthDays = yearMonth.lengthOfMonth()
        binding.npDay.minValue = 1
        binding.npDay.maxValue = currentMonthDays
        binding.npDay.value = currentDate
        binding.npDay.setOnValueChangedListener { picker, oldVal, newVal ->
            currentDate = newVal
        }
    }
    override fun show() {
        super.show()
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnOke.setOnClickListener {
            date = formatDate(currentYear,currentMonth,currentDate)
            listener.clickDateListener(date)
            dismiss()
        }
    }

    override fun dismiss() {
        super.dismiss()
        listener.hideNavigation()
    }
}


