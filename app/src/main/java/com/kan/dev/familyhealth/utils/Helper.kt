package com.kan.dev.familyhealth.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.kan.dev.familyhealth.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.EnumMap


fun openWebPage(context: Context, url: String) {
    try {
        val intent = Uri.parse(url).buildUpon().build()
        context.startActivity(Intent(Intent.ACTION_VIEW, intent))
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

fun shareApp(context: Context) {
    val pInfo: PackageInfo =
        context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
    val appPackageName = pInfo.packageName

    val appName = context.getString(R.string.app_name)
    val shareBodyText = "https://play.google.com/store/apps/details?id=$appPackageName"

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, appName)
        putExtra(Intent.EXTRA_TEXT, shareBodyText)
    }
    context.startActivity(Intent.createChooser(sendIntent, null))
}

@SuppressLint("NewApi")
fun formatDate(year: Int, month: Int, day: Int): String {
    val date = LocalDate.of(year, month, day)
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    return date.format(formatter)
}

fun reviewApp(context: Context) {
    val manager: ReviewManager = ReviewManagerFactory.create(context)
    val request: Task<ReviewInfo> = manager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val reviewInfo = task.result
            Log.e("ReviewInfo", "" + reviewInfo.toString())
            val flow: Task<Void> = manager.launchReviewFlow(context as Activity, reviewInfo)
            flow.addOnCompleteListener { task2 ->
                Log.e("ReviewSucces", "" + task2.toString())
                // checkExiting(context, interstitialAd)
                System.exit(0)
            }
        } else {
            System.exit(0)
        }
    }
}


@Throws(WriterException::class)
fun generateQRCode(context: Context, textToEncode: String?, qrCodeWidthAndHeight: Int): Bitmap {
    val hints: MutableMap<EncodeHintType, Any?> = EnumMap(
        EncodeHintType::class.java
    )
    hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
    val qrCodeWriter = QRCodeWriter()
    val bitMatrix = qrCodeWriter.encode(
        textToEncode,
        BarcodeFormat.QR_CODE,
        qrCodeWidthAndHeight,
        qrCodeWidthAndHeight,
        hints
    )
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(
                x,
                y,
                if (bitMatrix[x, y]) context.resources.getColor(R.color.black) else context.resources.getColor(
                    R.color.white
                )
            )
        }
    }
    return bitmap
}


fun getCurrentMonth(context: Context): String {
    var monthName = ""
    monthName = when (currentMonth) {
        1 -> context.getString(R.string.January)
        2 -> context.getString(R.string.February)
        3 -> context.getString(R.string.March)
        4 -> context.getString(R.string.April)
        5 -> context.getString(R.string.May)
        6 -> context.getString(R.string.June)
        7 -> context.getString(R.string.July)
        8 -> context.getString(R.string.August)
        9 -> context.getString(R.string.September)
        10 -> context.getString(R.string.October)
        11 -> context.getString(R.string.November)
        12 -> context.getString(R.string.December)
        else -> ""
    }
    return monthName
}

@SuppressLint("SimpleDateFormat")
fun formatDateFromLong(timeInMillis: Long): String? {
    val date = Date(timeInMillis)
    val sdf = SimpleDateFormat("HH:mm dd:MM:yy")
    return sdf.format(date)
}