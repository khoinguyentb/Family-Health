package com.kan.dev.familyhealth.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.kan.dev.familyhealth.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter



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