package com.kan.dev.familyhealth.ui.activity.interaction

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.os.Handler
import android.view.Gravity
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.zxing.WriterException
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.databinding.ActivityShareInformationBinding
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.generateQRCode
import com.kan.dev.familyhealth.utils.isClick
import com.kan.dev.familyhealth.utils.toastDuration
import java.io.File
import java.io.IOException

class ShareInformationActivity : BaseActivity<ActivityShareInformationBinding>() {
    override fun setViewBinding(): ActivityShareInformationBinding {
        return ActivityShareInformationBinding.inflate(layoutInflater)
    }
    private var lastTime: Long = 0
    var settings = false
    private var clipboard: ClipboardManager? = null
    private var file: File? = null
    override fun initData() {
        settings = intent.getBooleanExtra("settings", false)
        binding.apply {
            btnContinue.isSelected = true
            btnSecurity.isSelected = true
            clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            if (settings) {
                txt1.gravity = Gravity.START
                txt1.text = getString(R.string.my_security)
                btnBack.setImageResource(R.drawable.arrow_back)
                btnBack.imageTintList = ColorStateList.valueOf(getColor(R.color.black))
                btnSecurity.text = getString(R.string.share_qr)
                btnContinue.text = getString(R.string.share_code)
                btnBack.setOnClickListener { finish() }
            }

            binding.txtCode.text = sharePre.getString(MY_CODE,"")
            val qrCodeWidthAndHeight = 500
            val tempFile = File(externalCacheDir, "qr_code.png")

            try {
                val bitmap: Bitmap = generateQRCode(applicationContext, sharePre.getString(MY_CODE,""), qrCodeWidthAndHeight)
                imgMyQRCode.setImageBitmap(bitmap)
            } catch (e: WriterException) {
                throw RuntimeException(e)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

    override fun initView() {
        
    }

    override fun initListener() {
        binding.btnCopy.setOnClickListener { actionCopyCode("code") }
        binding.btnSecurity.setOnClickListener {
            if (isClick) {
                isClick = false
                if (settings) actionCopyCode("qr") else actionCopyCode("security")
                Handler().postDelayed({ isClick = true }, 500)
            }
        }
        binding.btnContinue.setOnClickListener {
            if (isClick) {
                isClick = false
                if (settings) actionCopyCode("security") else {
                    startActivity(
                        Intent(
                            applicationContext,
                            JoinWithFamilyActivity::class.java
                        )
                    )
                    finish()
                }
                Handler().postDelayed({ isClick = true }, 500)
            }
        }
    }

    private fun actionCopyCode(code: String) {
        val content: String
        when (code) {
            "code" -> {
                content = binding.txtCode.text.toString()
                val clip = ClipData.newPlainText("label", content)
                clipboard!!.setPrimaryClip(clip)
                if (System.currentTimeMillis() - lastTime > toastDuration) {
                    Toast.makeText(
                        this,
                        getString(R.string.copied),
                        Toast.LENGTH_SHORT
                    ).show()
                    lastTime = System.currentTimeMillis()
                }
            }

            "security" -> {
                if (settings) binding.btnContinue.isEnabled =
                    false else binding.btnSecurity.isEnabled =
                    false
                Handler().postDelayed({
                    binding.btnSecurity.isEnabled = true
                    binding.btnContinue.isEnabled = true
                }, 2000)
                content = (getString(R.string.download_app) + " "
                        + getString(R.string.link_share_app) + packageName) + " " + binding.txtCode.text.toString()
                val i = Intent(Intent.ACTION_SEND)
                i.setType("text/plain")
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                i.putExtra(Intent.EXTRA_TEXT, content)
                startActivity(Intent.createChooser(i, getString(R.string.share_with)))
            }

            "qr" -> {

                binding.btnSecurity.isEnabled = false
                Handler().postDelayed({
                    binding.btnSecurity.isEnabled = true
                }, 2000)
                val i = Intent(Intent.ACTION_SEND)
                i.setType("application/image")
                i.putExtra(
                    Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                        applicationContext, "$packageName.provider",
                        file!!
                    )
                )
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(Intent.createChooser(i, getString(R.string.share_with)))
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }
}