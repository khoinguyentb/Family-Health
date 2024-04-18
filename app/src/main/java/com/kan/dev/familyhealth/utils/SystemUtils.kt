package com.kan.dev.familyhealth.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemUtils @Inject constructor(private val context: Context) {
    private var myLocale: Locale? = null
    fun saveLocale( lang: String?) {
        setPreLanguage(lang)
    }

    fun setLocale() {
        val language = getPreLanguage()
        if (language == "") {
            val config = Configuration()
            val locale = Locale.getDefault()
            Locale.setDefault(locale)
            config.locale = locale
            context.resources
                .updateConfiguration(config, context.resources.displayMetrics)
        } else {
            changeLang(language)
        }
    }

    fun changeLang(lang: String?) {
        if (lang.equals("", ignoreCase = true)) return
        myLocale = Locale(lang)
        saveLocale(lang)
        Locale.setDefault(myLocale)
        val config = Configuration()
        config.locale = myLocale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun getPreLanguage(): String {
        val preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        return preferences.getString("KEY_LANGUAGE", "en")!!
    }

    fun setPreLanguage(language: String?) {
        if (language == null || language == "") {
        } else {
            val preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString("KEY_LANGUAGE", language)
            editor.apply()
        }
    }
}