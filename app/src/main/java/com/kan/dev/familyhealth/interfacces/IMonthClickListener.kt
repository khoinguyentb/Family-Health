package com.kan.dev.familyhealth.interfacces

interface IMonthClickListener {
    fun onClickListener(month: String?, index: Int)
    fun onClickYearListener(year: Int)
    fun onHideSystemBar()
}