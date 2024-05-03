package com.kan.dev.familyhealth.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.StringTokenizer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharePreferencesUtils @Inject constructor(context: Context) {

    private var mSharePref: SharedPreferences? = context.getSharedPreferences(APPLICATION, Context.MODE_PRIVATE)

    fun putString(str: String?, str2: String?) {
        val edit = mSharePref!!.edit()
        edit.putString(str, str2)
        edit.apply()
    }

    fun putInt(str: String?, int: Int?) {
        val edit = mSharePref!!.edit()
        edit.putInt(str, int!!)
        edit.apply()
    }

    fun getInt(str: String?, str2: Int?): Int {
        return mSharePref!!.getInt(str, str2!!)
    }

    fun getString(str: String?, str2: String?): String? {
        return mSharePref!!.getString(str, str2)
    }

    fun putBoolean(str: String?, bl: Boolean?) {
        val edit = mSharePref!!.edit()
        bl?.let { edit.putBoolean(str, it) }
        edit.apply()
    }

    fun getBoolean(str: String, bl: Boolean): Boolean {
        return bl.let { mSharePref!!.getBoolean(str, it) }
    }


    fun isRated(context: Context): Boolean {
        val pre = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        return pre.getBoolean("rated", false)
    }

    fun forceRated(context: Context) {
        val pre = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor = pre.edit()
        editor.putBoolean("rated", true)
        editor.apply()
    }

    fun getDecimalFormattedString(value: String): String {
        val lst = StringTokenizer(value, ".")
        var str1 = value
        var str2 = ""
        if (lst.countTokens() > 1) {
            str1 = lst.nextToken()
            str2 = lst.nextToken()
        }
        var str3 = ""
        var i = 0
        var j = -1 + str1.length
        if (str1[-1 + str1.length] == '.') {
            j--
            str3 = "."
        }
        var k = j
        while (true) {
            if (k < 0) {
                if (str2.length > 0) str3 = "$str3.$str2"
                return str3
            }
            if (i == 3) {
                str3 = ",$str3"
                i = 0
            }
            str3 = str1[k].toString() + str3
            i++
            k--
        }
    }

}