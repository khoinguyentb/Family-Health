package com.kan.dev.familyhealth.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.kan.dev.familyhealth.databinding.DialogDeleteBinding
import com.kan.dev.familyhealth.interfacces.IDeleteClickListener
import com.kan.dev.familyhealth.utils.SharePreferencesUtils

class DialogDelete (private val context: Context,private val listener: IDeleteClickListener) : Dialog(context) {
    private lateinit var sharePre : SharePreferencesUtils
    private val binding by lazy {
        DialogDeleteBinding.inflate(layoutInflater)
    }

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
        binding.btnCancel.isSelected = true
        binding.btnDelete.isSelected = true
    }


    override fun show() {
        super.show()
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnDelete.setOnClickListener {
            listener.clickDelete()
            dismiss()
        }
    }

    override fun dismiss() {
        super.dismiss()
        listener.hideNavigation()
    }
}