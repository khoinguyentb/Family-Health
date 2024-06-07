package com.kan.dev.familyhealth.ui.activity.main

import android.content.Intent
import android.os.Build
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport.Session.Event.Log
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.adapter.DetailListeners
import com.kan.dev.familyhealth.adapter.FriendDetailAdapter
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.RealtimeDAO.getOnetimeData
import com.kan.dev.familyhealth.data.RealtimeDAO.initRealtimeData
import com.kan.dev.familyhealth.data.model.FriendModel
import com.kan.dev.familyhealth.databinding.ActivityFriendBinding
import com.kan.dev.familyhealth.ui.activity.QRScanActivity
import com.kan.dev.familyhealth.ui.activity.interaction.JoinWithFamilyActivity
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick
import com.kan.dev.familyhealth.viewmodel.FriendViewModel
import com.lvt.ads.util.Admob
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class FriendActivity : BaseActivity<ActivityFriendBinding>() {
    override fun setViewBinding(): ActivityFriendBinding {
        return ActivityFriendBinding.inflate(layoutInflater)
    }

    private lateinit var myCode: String
    private val viewModel: FriendViewModel by viewModels()
    private lateinit var adapter: FriendDetailAdapter
    override fun initData() {
        myCode = sharePre.getString(MY_CODE, "")!!
        initRealtimeData()
    }

    override fun initView() {
        Admob.getInstance()
            .loadBannerFragment(this, getString(R.string.banner_all), binding.includeBanner)
        binding.btnQR.isSelected = true
        binding.edtSearch.clearFocus()
    }

    override fun initListener() {
        binding.btnBack.setOnClickListener {  onBackPressed() }
        binding.btnAddCode.setOnClickListener {
            if (isClick) {
                isClick = false
                startActivity(Intent(applicationContext, JoinWithFamilyActivity::class.java))
                Handler().postDelayed({ isClick = true }, 500)
            }
        }
        binding.btnQR.setOnClickListener {
            if (isClick) {
                isClick = false
                startActivity(
                    Intent(
                        applicationContext,
                        QRScanActivity::class.java
                    ).putExtra("addfriend", true)
                )
                handler.postDelayed({ isClick = true }, 500)
            }
        }
        actionSearchFriend()
    }

    override fun onResume() {
        super.onResume()
        getFriend()
        binding.edtSearch.clearFocus()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }

    private fun getFriend() {
        viewModel.getAll.observe(this) {
            android.util.Log.d("KanMobile",it.size.toString())

            binding.recyclerFriend.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.recyclerFriend.setHasFixedSize(true)
            if (it.isEmpty()) {
                binding.imvNull.visibility = View.VISIBLE
                binding.tvNull.visibility = View.VISIBLE
            } else {

                adapter = FriendDetailAdapter(this, object : DetailListeners {
                    override fun onDelete(item: FriendModel) {
                    }
                })
                adapter.setItems(it)
                binding.recyclerFriend.adapter = adapter
                binding.recyclerFriend.itemAnimator = null

                binding.imvNull.visibility = View.GONE
                binding.tvNull.visibility = View.GONE
            }
        }
    }

    private var name = ""
    private var nickname = ""
    private var phoneNumber = ""

    private fun actionSearchFriend() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val text = charSequence.toString().trim { it <= ' ' }
                val tempList: MutableList<FriendModel> = ArrayList<FriendModel>()
                viewModel.getAll.observe(this@FriendActivity) { it ->
                    for (u in it) {
                        getOnetimeData(myCode + "/friends/" + u.code) { snapshot ->
                            name = snapshot!!.child("name").getValue(String::class.java)!!
                            nickname = snapshot.child("nickname").getValue(String::class.java)!!
                            phoneNumber = snapshot.child("phoneNumber").getValue(String::class.java)!!
                            if (nickname.trim { it <= ' ' } == "") {
                                if (phoneNumber.contains(text) || name.lowercase(Locale.getDefault())
                                        .contains(text.lowercase(Locale.getDefault()))
                                ) {
                                    tempList.add(u)
                                }
                                if (tempList.size == 0) {
                                    binding.imvNull.visibility = View.VISIBLE
                                    binding.tvNull.visibility = View.VISIBLE
                                } else {
                                    binding.imvNull.visibility = View.GONE
                                    binding.tvNull.visibility = View.GONE
                                }
                            } else {
                                if (phoneNumber.contains(text) || name.lowercase(Locale.getDefault())
                                        .contains(text.lowercase(Locale.getDefault())) || nickname.lowercase(
                                        Locale.getDefault()
                                    ).contains(text.lowercase(Locale.getDefault()))
                                ) {
                                    tempList.add(u)
                                }
                            }
                            if (tempList.size == 0) {
                                binding.imvNull.visibility = View.VISIBLE
                                binding.tvNull.visibility = View.VISIBLE
                            } else {
                                binding.imvNull.visibility = View.GONE
                                binding.tvNull.visibility = View.GONE
                            }
                        }
                    }
                    if (tempList.size == 0) {
                        binding.imvNull.visibility = View.VISIBLE
                        binding.tvNull.visibility = View.VISIBLE
                    } else {
                        binding.imvNull.visibility = View.GONE
                        binding.tvNull.visibility = View.GONE
                    }
                    adapter = FriendDetailAdapter(this@FriendActivity, object : DetailListeners {
                        override fun onDelete(item: FriendModel) {
                        }
                    })
                    binding.recyclerFriend.adapter = adapter
                    binding.recyclerFriend.itemAnimator = null
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding.edtSearch.setOnEditorActionListener { textView, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                val imm =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(textView.windowToken, 0)
                binding.edtSearch.clearFocus()
                return@setOnEditorActionListener true
            }
            false
        }
    }

}