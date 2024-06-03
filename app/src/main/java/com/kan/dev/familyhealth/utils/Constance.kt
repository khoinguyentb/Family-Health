package com.kan.dev.familyhealth.utils

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate


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
const val KEY_MAP = "KEY_MAP"
const val OTHER = "OTHER"
const val DISABLE = "disable"
const val ENABLE = "enable"
const val BUS = "bus"
const val PETROL = "gas_station"
const val CAFE = "cafe"
const val CINEMA = "movie_theater"
const val BANK = "bank"


const val toastDuration = 2000
 var isClick = true
 val handler by lazy {
    Handler(Looper.getMainLooper())
}

val DEFAULT_LATLNG = LatLng(-1.0, -1.0)
var daily : Boolean = true
var weekly: Boolean = false
var monthly: Boolean = false
var date: String? = null
var months = 0
var years = 0
var Code = ""
@SuppressLint("NewApi")
var currentMonth = LocalDate.now().monthValue

@SuppressLint("NewApi")
var currentYear = LocalDate.now().year