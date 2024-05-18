package com.kan.dev.familyhealth.base

import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.ui.activity.SplashActivity
import com.lvt.ads.util.AdsApplication
import com.lvt.ads.util.AppOpenManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : AdsApplication() {
    override fun onCreate() {
        super.onCreate()
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)

    }

    override fun enableAdsResume(): Boolean {
        return true
    }

    override fun getListTestDeviceId(): MutableList<String>? {
        return null
    }

    override fun getResumeAdId(): String {
        return getString(R.string.appopen_resume)
    }

    override fun buildDebug(): Boolean {
        return true
    }
}