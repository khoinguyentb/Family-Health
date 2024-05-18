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
const val CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQQRSTUVWXYZ0123456789"
const val CODE_LENGTH = 10
const val MY_CODE = "CODE"
const val KEY_QR_BITMAP = "qr_bitmap_link"
const val QR_REQUEST_CODE = 123
const val PHONE_PATTERN = "^[+]?[0-9]{10,13}$"

const val OTHER = "OTHER"
const val toastDuration = 2000
 var isClick = true
 val handler by lazy {
    Handler(Looper.getMainLooper())
}