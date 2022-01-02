package com.lucasginard.airelibre.modules

import android.app.Application
import com.lucasginard.airelibre.modules.data.SharedPref

class AireLibreApp: Application() {
    companion object {
        lateinit var prefs: SharedPref
    }

    override fun onCreate() {
        super.onCreate()
        prefs = SharedPref(applicationContext)
    }
}