package com.kan.dev.familyhealth.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.view.WindowCompat
import com.kan.dev.familyhealth.databinding.DialogDeleteFriendBinding

class DialogDeleteFriend(var context: Activity, private val listener: OnclickDeleteListener) : Dialog(context)  {
    private val binding by lazy {
        DialogDeleteFriendBinding.inflate(layoutInflater)
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
//        window?.setBackgroundDrawable(context.getDrawable(R.drawable.custum_dialog))
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

    override fun show() {
        super.show()
        binding.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnOk.setOnClickListener {
                listener.onClickDelete()
                dismiss()
            }
        }
    }
    override fun dismiss() {
        super.dismiss()
        hideSystemUI()
    }
}

interface OnclickDeleteListener {
    fun onClickDelete()
}