package com.kan.dev.familyhealth.utils

import android.os.Handler
import android.os.Looper


const val APPLICATION = "KEY"
const val IS_LANGUAGE = "IS_LANGUAGE"
const val SELECT_RATE = "SELECT_RATE"
const val LOG_APP = "LOG_APP"
const val CHECK_PER = "CHECK_PER"
const val MALE = "MALE"
const val FEMALE = "FEMALE"
const val DATE_CHANGE = "DATE_CHANGE"
const val BMIS = "BMI"

 var isClick = true
 val handler by lazy {
    Handler(Looper.getMainLooper())
}