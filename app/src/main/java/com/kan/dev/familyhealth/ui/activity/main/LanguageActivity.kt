package com.kan.dev.familyhealth.ui.activity.main

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kan.dev.familyhealth.adapter.LanguageAdapter
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.Data
import com.kan.dev.familyhealth.data.model.LanguageModel
import com.kan.dev.familyhealth.databinding.ActivityLanguageBinding
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick
import com.kan.dev.familyhealth.viewmodel.LanguageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageActivity : BaseActivity<ActivityLanguageBinding>(),LanguageAdapter.ILanguageItem {
    override fun setViewBinding(): ActivityLanguageBinding {
        return ActivityLanguageBinding.inflate(layoutInflater)
    }
    private lateinit var codeLang : String
    private val viewModel : LanguageViewModel by viewModels()
    private val adapter : LanguageAdapter by lazy {
        LanguageAdapter(this,this)
    }
    private lateinit var intent: Intent
    private val layoutManager : RecyclerView.LayoutManager by lazy {
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
    override fun initData() {
        codeLang = viewModel.getCodeLang()
        Log.d("KanMobile",viewModel.getCodeLang())
        initListLanguage()
        initListener()
    }

    override fun initView() {
        binding.apply {
            adapter.setItems(Data.languageList)
            adapter.setCheck(codeLang)
            rcvLanguge.layoutManager = layoutManager
            rcvLanguge.adapter = adapter
        }
    }

    override fun initListener() {
        binding.apply {
            imgDone.setOnClickListener {
                if (isClick){
                    isClick = false
                    viewModel.setLang(codeLang)
                    if (viewModel.isLanguage()){
                        intent = Intent(this@LanguageActivity, MainActivity::class.java)
                    }else{
                        viewModel.isFirstLang()
                        intent = Intent(this@LanguageActivity, IntroActivity::class.java)
                        Log.d("isFirstLang","isFirstLang")
                    }
                    startActivity(intent)
                    finishAffinity()
                    handler.postDelayed({isClick = true},500)
                }
            }
        }
    }
    fun initListLanguage(): List<LanguageModel>{
        val codeLang = viewModel.getCodeLang()
        val listLanguage = Data.languageList
        for (i in 0 until listLanguage.size){
            if (listLanguage[i].code == codeLang) {
                val selectedLanguage = listLanguage[i]
                listLanguage.removeAt(i)
                listLanguage.add(0, selectedLanguage)
                break
            }
        }
        return listLanguage
    }
    override fun onClickItemLanguage(code: String) {
        codeLang = code
    }

}