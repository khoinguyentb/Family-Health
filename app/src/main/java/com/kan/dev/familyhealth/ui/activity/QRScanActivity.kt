package com.kan.dev.familyhealth.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.databinding.ActivityQrscanBinding
import com.kan.dev.familyhealth.ui.activity.interaction.JoinWithFamilyActivity
import com.kan.dev.familyhealth.ui.activity.main.MainActivity
import com.kan.dev.familyhealth.utils.DialogListener
import com.kan.dev.familyhealth.utils.DialogUtils
import com.kan.dev.familyhealth.utils.checkPer
import com.kan.dev.familyhealth.utils.checkPerList
import com.kan.dev.familyhealth.utils.handler
import com.lvt.ads.util.Admob
import com.lvt.ads.util.AppOpenManager
import java.io.IOException

class QRScanActivity : BaseActivity<ActivityQrscanBinding>() {
    override fun setViewBinding(): ActivityQrscanBinding {
        return ActivityQrscanBinding.inflate(layoutInflater)
    }

    private var scanner: CodeScanner? = null
    private var perDialog: Dialog? = null
    private var isClick = true
    private var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>? = null
    val CAMERA_PER = arrayOf(
        Manifest.permission.CAMERA
    )
    override fun initData() {

    }

    override fun initView() {
        Admob.getInstance()
            .loadBannerFragment(this, getString(R.string.banner_all), binding.includeBanner)
        initDialog()
        scanner = CodeScanner(applicationContext, binding.scannerView)
        initImage()
        binding.btnAddImage.isSelected = true
        binding.btnAddCode.isSelected = true
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    override fun initListener() {

        binding.btnAddCode.setOnClickListener {
            if (isClick) {
                isClick = false
                val intent = intent
                val data = intent.getBooleanExtra("addfriend", false)
                if (data) {
                    startActivity(Intent(this, JoinWithFamilyActivity::class.java))
                    finish()
                } else {
                    finish()
                }
                handler.postDelayed({ isClick = true }, 500)
            }
        }
        binding.btnBack.setOnClickListener {
            if (isClick) {
                isClick = false
                finish()
                handler.postDelayed({ isClick = true }, 500)
            }
        }
        binding.btnAddImage.setOnClickListener {
            if (isClick) {
                isClick = false
                actionChooseImage()
                handler.postDelayed({ isClick = true }, 500)
            }
        }
    }
    override fun onStart() {
        super.onStart()
        check()
    }

    override fun onResume() {
        super.onResume()
        AppOpenManager.getInstance().enableAppResumeWithActivity(QRScanActivity::class.java)
        AppOpenManager.getInstance().enableAppResumeWithActivity(MainActivity::class.java)
        if (scanner != null) {
            scanner!!.startPreview()
        }
    }
    private fun check() {
        if (checkPerList(CAMERA_PER)) {
            initScanner()
            binding.scannerView.visibility = View.GONE
            perDialog!!.dismiss()
            Handler().postDelayed({
                binding.scannerView.visibility = View.VISIBLE
            }, 500)
        } else {
            perDialog!!.show()
        }
    }
    private fun actionChooseImage() {
        pickMedia!!.launch(PickVisualMediaRequest())
    }

    private fun initDialog() {

        perDialog = DialogUtils.initPerDialog(this, object : DialogListener {
            override fun onGrant() {
                var check = false
                for (code in CAMERA_PER) {
                    if (!checkPer(code, this@QRScanActivity)) {
                        check = true
                    }
                }
                if (check) {
                    AppOpenManager.getInstance()
                        .disableAppResumeWithActivity(QRScanActivity::class.java)
                    val intent = Intent()
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.setData(uri)
                    startActivity(intent)
                } else {
                    initScanner()
                    perDialog!!.dismiss()
                    AppOpenManager.getInstance()
                        .enableAppResumeWithActivity(QRScanActivity::class.java)
                }
            }

            override fun onDismiss() {}
        })
    }
    private fun initImage() {
        pickMedia = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { result ->
            if (result != null) {
                val mimeType = contentResolver.getType(result)
                if (mimeType != null) {
                    if (mimeType.startsWith("image/")) {
                        try {
                            val bitmap =
                                MediaStore.Images.Media.getBitmap(contentResolver, result)
                            val qrContent: String = decodeQRCode(bitmap)!!
                            if (qrContent != null) {
                                Log.d("QRCode Content", qrContent)
                                val intent = Intent(this, JoinWithFamilyActivity::class.java)
                                intent.putExtra("codeFri", qrContent)
                                startActivity(intent)
                                finish()
                            } else {
                                Log.d("QRCode", "Không tìm thấy mã QR trong hình ảnh")
                                Toast.makeText(this, getString(R.string.No_QR), Toast.LENGTH_LONG)
                                    .show()
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else if (mimeType.startsWith("video/")) {
                        Toast.makeText(this, getString(R.string.Error_QR), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, getString(R.string.Error_QR), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    @SuppressLint("ObsoleteSdkInt")
    private fun initScanner() {
        scanner!!.decodeCallback = DecodeCallback { result: Result ->
            runOnUiThread {
                if (scanner!!.isPreviewActive) scanner!!.stopPreview()
                Handler().postDelayed({
                    val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                    if (vibrator.hasVibrator()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(
                                VibrationEffect.createOneShot(
                                    500,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                        } else {
                            vibrator.vibrate(500)
                        }
                    }
                    actionSendCodeBack(result.text)
                }, 500)
            }
        }
    }
    private fun actionSendCodeBack(result: String) {
        val resultIntent = Intent()
        resultIntent.putExtra("code", result)
        val intent = intent
        val data = intent.getBooleanExtra("addfriend", false)
        setResult(RESULT_OK, resultIntent)
        finish()
        if (data) {
            val intent1 = Intent(this, JoinWithFamilyActivity::class.java)
            intent1.putExtra("codeFri", result)
            startActivity(intent1)
            finish()
        }
    }
    private fun decodeQRCode(bitmap: Bitmap): String? {
        return try {
            val intArray = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader()
            val result = reader.decode(binaryBitmap)
            result.text
        } catch (e: NotFoundException) {
            Log.e("QRDecode", "Không tìm thấy mã QR trong hình ảnh", e)
            null
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (scanner != null) scanner!!.releaseResources()
    }
}