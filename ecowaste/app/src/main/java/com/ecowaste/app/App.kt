package com.ecowaste.app

import android.app.Application
import android.util.Log
import com.ecowaste.app.data.local.LocalStorage

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            LocalStorage.init(this)
            Log.i("App", "LocalStorage initialized for demo mode")
        } catch (e: Exception) {
            Log.e("App", "LocalStorage initialization failed", e)
        }
    }
}

