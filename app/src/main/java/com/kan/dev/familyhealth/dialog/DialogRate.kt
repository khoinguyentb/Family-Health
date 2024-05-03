package com.kan.dev.st048_bmicalculator.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.databinding.DialogRateBinding
import com.kan.dev.familyhealth.utils.SharePreferencesUtils
import com.kan.dev.familyhealth.utils.reviewApp

class DialogRate(private val context: Context, private val listener: OnDialogDismissListener) : Dialog(context) {
    var rating: Float = 0F
    private val bindingDialogRate by lazy {
        DialogRateBinding.inflate(layoutInflater)
    }
    private lateinit var sharePre : SharePreferencesUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindingDialogRate.root)
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
    }

    override fun show() {
        super.show()
        bindingDialogRate.ratingBar.rating = 0f
        bindingDialogRate.btnRate.setOnClickListener {
            if (rating > 3){
                sharePre.forceRated(context)
                reviewApp(context)
            }
            dismiss()
        }

        bindingDialogRate.tvExit.setOnClickListener {

            dismiss()
        }

        bindingDialogRate.ratingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
            this.rating = rating
            when (rating) {
                0F -> {
                    bindingDialogRate.img.setImageResource(R.drawable.rate_0)
                    bindingDialogRate.tvTitle.text = context.getString(R.string.title_rate_0)
                    bindingDialogRate.tvDes.text = context.getString(R.string.des_rate_0)
                }

                1F -> {
                    bindingDialogRate.img.setImageResource(R.drawable.rate_1)
                    bindingDialogRate.tvTitle.text = context.getString(R.string.title_rate_1)
                    bindingDialogRate.tvDes.text = context.getString(R.string.des_rate_1)
                }

                2F -> {
                    bindingDialogRate.img.setImageResource(R.drawable.rate_2)
                    bindingDialogRate.tvTitle.text = context.getString(R.string.title_rate_2)
                    bindingDialogRate.tvDes.text = context.getString(R.string.des_rate_2)
                }

                3F -> {
                    bindingDialogRate.img.setImageResource(R.drawable.rate_3)
                    bindingDialogRate.tvTitle.text = context.getString(R.string.title_rate_3)
                    bindingDialogRate.tvDes.text = context.getString(R.string.des_rate_3)
                }

                4F -> {
                    bindingDialogRate.img.setImageResource(R.drawable.rate_4)
                    bindingDialogRate.tvTitle.text = context.getString(R.string.title_rate_4)
                    bindingDialogRate.tvDes.text = context.getString(R.string.des_rate_4)
                }

                5F -> {
                    bindingDialogRate.img.setImageResource(R.drawable.rate_5)
                    bindingDialogRate.tvTitle.text = context.getString(R.string.title_rate_5)
                    bindingDialogRate.tvDes.text = context.getString(R.string.des_rate_5)
                }
            }
        }
    }


    override fun dismiss() {
        super.dismiss()
        listener.onDialogDismiss()
    }
}

interface OnDialogDismissListener {
    fun onDialogDismiss()
}
