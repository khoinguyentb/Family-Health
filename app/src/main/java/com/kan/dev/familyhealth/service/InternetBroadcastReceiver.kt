package com.kan.dev.familyhealth.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.kan.dev.familyhealth.ui.activity.NoInternetActivity


class InternetBroadcastReceiver : BroadcastReceiver() {
    companion object {
        var isRegistered = false
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        if (!isNetworkConnected(context)) {
            val i = Intent(context, NoInternetActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        } else {
            val closeIntent = Intent(context, NoInternetActivity::class.java)
            closeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            closeIntent.putExtra("EXIT", true)
            context.startActivity(closeIntent)
        }
    }

    private fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }
}

// Example usage
fun registerReceiver(context: Context) {
    if (!InternetBroadcastReceiver.isRegistered){
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        val receiver = InternetBroadcastReceiver()
        context.registerReceiver(receiver, filter)
        InternetBroadcastReceiver.isRegistered = true
    }
}

fun unregisterReceiver(context: Context, receiver: InternetBroadcastReceiver) {
    if (InternetBroadcastReceiver.isRegistered) {
        context.unregisterReceiver(receiver)
        InternetBroadcastReceiver.isRegistered = false
    }
}
