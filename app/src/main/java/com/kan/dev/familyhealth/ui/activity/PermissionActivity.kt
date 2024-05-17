package com.kan.dev.familyhealth.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.databinding.ActivityPermissionBinding
import com.kan.dev.familyhealth.utils.CHECK_PER
import com.kan.dev.familyhealth.utils.LOG_APP
import com.kan.dev.familyhealth.utils.SharePreferencesUtils
import com.kan.dev.familyhealth.utils.checkPermissionCamera
import com.kan.dev.familyhealth.utils.checkPermissionLocation
import com.kan.dev.familyhealth.utils.checkPermissionNotification
import com.kan.dev.familyhealth.utils.requestAppPermissionCamera
import com.kan.dev.familyhealth.utils.requestAppPermissionLocation
import com.kan.dev.familyhealth.utils.requestAppPermissionNotification
import com.kan.dev.familyhealth.utils.showPermissionAgreeDialog
import com.kan.dev.familyhealth.utils.showPermissionSettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PermissionActivity : BaseActivity<ActivityPermissionBinding>() {
    override fun setViewBinding(): ActivityPermissionBinding {
        return ActivityPermissionBinding.inflate(layoutInflater)
    }
    companion object {
        const val REQUEST_CODE_CAMERA = 1000
        const val REQUEST_CODE_NOTIFICATION = 1001
        const val REQUEST_CODE_LOCATION = 1002
    }

    override fun initData() {
        sharePre.putBoolean(CHECK_PER,true)
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkPermissionNotification()) {
                    swNotification.setImageResource(R.drawable.switch_on)
                } else {
                    swNotification.setImageResource(R.drawable.switch_off)
                }
            }
            if (checkPermissionCamera()) {
                swCamera.setImageResource(R.drawable.switch_on)
            } else {
                swCamera.setImageResource(R.drawable.switch_off)
            }

            if (checkPermissionLocation()) {
                swLocation.setImageResource(R.drawable.switch_on)
            } else {
                swLocation.setImageResource(R.drawable.switch_off)
            }
        }
    }

    override fun initView() {
    }

    override fun initListener() {
        binding?.apply {
            swCamera.setOnClickListener {
                if (!checkPermissionCamera()){
                    requestAppPermissionCamera(REQUEST_CODE_CAMERA)
                }
            }
            swNotification.setOnClickListener {
                if (!checkPermissionNotification()) {
                    requestAppPermissionNotification(REQUEST_CODE_NOTIFICATION)
                }
            }

            swLocation.setOnClickListener {
                if (!checkPermissionLocation()) {
                    requestAppPermissionLocation(REQUEST_CODE_LOCATION)
                }
            }

            Continue.setOnClickListener{
                if (checkPermissionCamera() && checkPermissionNotification()){
                    sharePre.putBoolean(LOG_APP,true)
                    val intent = Intent(this@PermissionActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    showPermissionAgreeDialog()
                }
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_NOTIFICATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onResume()
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                        showPermissionSettingsDialog()
                    }
                }
            }
            REQUEST_CODE_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    onResume()
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        showPermissionSettingsDialog()
                    }
                }
            }
            REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    onResume()
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        showPermissionSettingsDialog()
                    }
                }
            }
        }
    }
}