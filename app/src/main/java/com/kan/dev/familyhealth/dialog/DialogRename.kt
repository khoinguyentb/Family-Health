package com.kan.dev.familyhealth.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowCompat
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.databinding.DialogRenameBinding

class DialogRename(var context: Activity, private val listener: OnclickListener, private val nickName: String) : Dialog(context) {

    private val binding by lazy {
        DialogRenameBinding.inflate(layoutInflater)
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
        binding.edtRename.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                binding.edtRename.setBackgroundResource(R.drawable.bg_edt)
            }
        }
        binding.edtRename.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                hideKeyboard(context)
                binding.edtRename.clearFocus()
                true
            }else false
        }
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

    private fun hideKeyboard(context: Context) {
        val view = binding.edtRename
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun show() {
        super.show()
        binding.apply {
            edtRename.setText(nickName)
            imgDelete.setOnClickListener {
                edtRename.text.clear()
            }
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnOk.setOnClickListener {
                listener.onClickRename(edtRename.text.toString().trim())
                dismiss()
//                if (edtRename.text != null){
//
//                }else{
//                    Toast.makeText(context,context.getString(R.string.nickname_cannot_be_empty),Toast.LENGTH_SHORT).show()
//                }
            }
        }
    }
    override fun dismiss() {
        super.dismiss()
        hideSystemUI()
    }
}

interface OnclickListener {
    fun onClickRename(nickName : String)
}