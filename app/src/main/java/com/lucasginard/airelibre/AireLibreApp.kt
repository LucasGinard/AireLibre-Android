package com.lucasginard.airelibre

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.lucasginard.airelibre.modules.data.SharedPref

class AireLibreApp: Application() {
    companion object {
        lateinit var prefs: SharedPref
    }

    override fun onCreate() {
        super.onCreate()
        prefs = SharedPref(applicationContext)
        if(BuildConfig.DEBUG){
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        }
    }
}