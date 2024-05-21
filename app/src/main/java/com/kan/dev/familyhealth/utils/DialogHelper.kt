package com.kan.dev.familyhealth.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.databinding.DialogMapTypeBinding
import com.kan.dev.familyhealth.databinding.DialogPermissionBinding
import com.kan.dev.familyhealth.ui.activity.PermissionActivity.Companion.REQUEST_CODE_LOCATION


object DialogUtils {
    fun initPerDialog(context: Activity, dialogListener: DialogListener): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding: DialogPermissionBinding =
            DialogPermissionBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.getRoot())
        val window = dialog.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(context.getColor(R.color.transfer)))
        val attribute = window.attributes
        attribute.gravity = Gravity.CENTER
        window.attributes = attribute
        binding.txtAgree.setOnClickListener { view ->
//            dialog.dismiss();
            initView(context)
            dialogListener.onGrant()
        }
        dialog.setOnDismissListener { dialogInterface: DialogInterface? ->
            dialog.dismiss()
            initView(context)
            dialogListener.onDismiss()
        }
        dialog.setCancelable(false)
        return dialog
    }

    fun initLocationRequestDialog(context: Activity): AlertDialog {
        val buttonText = context.getString(R.string.Agree)
        val spannableButtonString = SpannableString(buttonText)
        spannableButtonString.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    context,
                    R.color.blue_1E93FF
                )
            ), 0, buttonText.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val builder = AlertDialog.Builder(context)
        builder.setMessage(context.getString(R.string.Message_perLocation))
            .setCancelable(false)
            .setNegativeButton(spannableButtonString) { dialogInterface: DialogInterface, i: Int ->
                if (Build.VERSION.SDK_INT >= 29){
                    ActivityCompat.requestPermissions(context, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION), REQUEST_CODE_LOCATION)
                }else{
                    ActivityCompat.requestPermissions(context, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE), REQUEST_CODE_LOCATION)
                }
                dialogInterface.dismiss()
            }
        return builder.create()
    }

    fun initMapTypeDialog(context: Activity, dialogListener: ClickDialogListener): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding: DialogMapTypeBinding =
            DialogMapTypeBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.getRoot())
        val window = dialog.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(context.getColor(R.color.transfer)))
        val attribute = window.attributes
        attribute.gravity = Gravity.BOTTOM
        window.attributes = attribute
        binding.layoutMap1.setOnClickListener { dialogListener.onClick(1) }
        binding.layoutMap2.setOnClickListener { dialogListener.onClick(2) }
        binding.layoutMap3.setOnClickListener { dialogListener.onClick(4) }
        binding.layoutMap4.setOnClickListener { dialogListener.onClick(3) }
        binding.btnClose.setOnClickListener {
            dialog.dismiss()
            dialogListener.onClose()
        }
        dialog.setOnDismissListener {
            dialogListener.onClose()
        }
        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        dialog.setCancelable(true)
        return dialog
    }

    fun initView(activity: Activity) {
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        } else {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }
}

interface DialogListener {
    fun onGrant()
    fun onDismiss()
}
interface ClickDialogListener{
    fun onClick(type:Int)
    fun onClose()
}
interface AddressListener{
    fun onAddress(address: String)
}
