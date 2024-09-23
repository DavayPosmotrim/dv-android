package com.davay.android.core.presentation

import android.app.Application
import com.davay.android.BuildConfig
import com.davay.android.di.AppComponentHolder
import com.my.tracker.MyTracker

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppComponentHolder.createComponent(this)
        initMyTracker()
    }

    private fun initMyTracker() {
        val trackerParams = MyTracker.getTrackerParams()
        val trackerConfig = MyTracker.getTrackerConfig()
        val sdkKey = BuildConfig.MYTRACKER_SDK_KEY

        MyTracker.initTracker(sdkKey, this)
    }
}