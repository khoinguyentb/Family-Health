package com.kan.dev.familyhealth.viewmodel

import androidx.lifecycle.ViewModel
import com.kan.dev.familyhealth.utils.IS_LANGUAGE
import com.kan.dev.familyhealth.utils.SharePreferencesUtils
import com.kan.dev.familyhealth.utils.SystemUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val sharePre : SharePreferencesUtils,
    private val systemUtils: SystemUtils
) : ViewModel(){

    fun getCodeLang() : String{
        return systemUtils.getPreLanguage()
    }
    fun setLang(code : String){
        systemUtils.setPreLanguage(code)
    }

    fun isLanguage():Boolean{
        return sharePre.getBoolean(IS_LANGUAGE,false)
    }
    fun isFirstLang(){
        sharePre.putBoolean(IS_LANGUAGE,true)
    }
}