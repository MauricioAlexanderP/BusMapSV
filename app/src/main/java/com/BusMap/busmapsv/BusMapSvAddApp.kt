package com.BusMap.busmapsv

import android.app.Application
import com.google.android.gms.ads.MobileAds

class BusMapSvAddApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}