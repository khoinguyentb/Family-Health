package com.kan.dev.familyhealth.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.kan.dev.familyhealth.adapter.AvatarAdapter
import com.kan.dev.familyhealth.adapter.ClickAvt
import com.kan.dev.familyhealth.databinding.DialogAvtBinding
import com.kan.dev.familyhealth.utils.SharePreferencesUtils
import com.kan.dev.familyhealth.utils.initAvatarList


class DialogAvt(var context: Activity, private val onSaveListener: OnSaveListener) : Dialog(context) {

    private val binding by lazy {
        DialogAvtBinding.inflate(layoutInflater)
    }
    private var avt : Int = 0
    private lateinit var sharePre: SharePreferencesUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.decorView?.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        window?.attributes?.gravity = Gravity.CENTER
        sharePre = SharePreferencesUtils(context)
        val list = initAvatarList(context)
        avt = list[sharePre.getInt("selectedItem",0)]
        val adapter = AvatarAdapter(context,object : ClickAvt{
            override fun listener(avt: Int) {
                this@DialogAvt.avt = avt
            }
        })
        adapter.setItems(list)
        val layoutManager = GridLayoutManager(context, 4)
        binding.rcvAVT.layoutManager = layoutManager
        binding.rcvAVT.adapter = adapter
    }

    fun hideSystemUI() {
        context.window!!.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(context.window!!, false)
        } else {
            context.window!!.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }
    override fun dismiss() {
        super.dismiss()
        hideSystemUI()
    }

    override fun show() {
        super.show()
        binding.icClose.setOnClickListener {
            dismiss()
        }
        binding.btnSave.setOnClickListener {
            onSaveListener.onClickSave(avt)
            dismiss()
        }
    }

}


interface OnSaveListener {
    fun onClickSave(avt : Int)
}