package com.kan.dev.familyhealth.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kan.dev.familyhealth.R

fun Activity.requestAppPermissionNotification(requestCode: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), requestCode)
    }
}

fun Activity.checkPerList(listPermission: Array<String>): Boolean {
    for (per in listPermission) {
        val allow = ActivityCompat.checkSelfPermission(
            this,
            per
        ) == PackageManager.PERMISSION_GRANTED
        if (!allow) return false
    }
    return true
}

fun checkPer(permission: String?, context: Context?): Boolean {
    return if (context != null) {
        ActivityCompat.checkSelfPermission(
            context,
            permission!!
        ) == PackageManager.PERMISSION_GRANTED
    } else false
}

fun Activity.requestAppPermissionCamera(requestCode: Int) {
    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), requestCode)
}

@RequiresApi(Build.VERSION_CODES.Q)
fun Activity.requestAppPermissionRECOGNITION(requestCode: Int) {
    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), requestCode)
}
fun Activity.requestAppPermissionLocation(requestCode: Int) {
    if (Build.VERSION.SDK_INT >= 29){
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION), requestCode)
    }else{
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE), requestCode)
    }
}

fun Activity.checkPermissionCamera(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}


fun Activity.checkPermissionRECOGNITION(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

@SuppressLint("InlinedApi")
fun Activity.checkPermissionLocation(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE).toString()
    ) == PackageManager.PERMISSION_GRANTED
}

fun Activity.checkPermissionNotification(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.POST_NOTIFICATIONS
        )
        notificationPermission == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

fun Context.showPermissionSettingsDialog() {
    val builder = AlertDialog.Builder(this, R.style.YourCustomAlertDialogTheme)
    builder.setTitle(R.string.title_dialog_permission)
        .setMessage(R.string.content_dialog_permission)
        .setPositiveButton(R.string.setting) { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        .setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

fun Activity.showPermissionAgreeDialog() {
    val builder = AlertDialog.Builder(this,R.style.YourCustomAlertDialogTheme)
    builder.setMessage(getString(R.string.content_dialog_permission))
        .setTitle(getString(R.string.title_dialog_permission))
        .setCancelable(false)
        .setPositiveButton(R.string.Agree) { p0, _ ->
            p0.dismiss()
        }
    builder.show()
}