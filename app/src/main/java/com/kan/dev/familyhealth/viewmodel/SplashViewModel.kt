package com.kan.dev.familyhealth.viewmodel

import androidx.lifecycle.ViewModel
import com.kan.dev.familyhealth.utils.CHECK_PER
import com.kan.dev.familyhealth.utils.IS_LANGUAGE
import com.kan.dev.familyhealth.utils.LOG_APP
import com.kan.dev.familyhealth.utils.SELECT_RATE
import com.kan.dev.familyhealth.utils.SharePreferencesUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sharedPre: SharePreferencesUtils
) : ViewModel() {
    private var checkLogApp : Int = 0


    fun isRateApp(){
        checkLogApp = sharedPre.getInt(SELECT_RATE,0)
        checkLogApp++
        sharedPre.putInt(SELECT_RATE,checkLogApp)
    }
    fun isFirstApp() : Boolean{
        return sharedPre.getBoolean(LOG_APP, false)
    }
    fun isFirstAppLang() : Boolean{
        return sharedPre.getBoolean(IS_LANGUAGE, false)
    }
    fun isFirstAppPer() : Boolean{
        return sharedPre.getBoolean(CHECK_PER, false)
    }
}