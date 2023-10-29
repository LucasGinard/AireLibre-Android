package com.lucasginard.airelibre

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.lucasginard.airelibre.modules.data.SharedPref
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AireLibreApp: Application() {

    companion object {
        lateinit var prefs: SharedPref
    }

    override fun onCreate() {
        super.onCreate()
        prefs = SharedPref(applicationContext)
        configureRemoteConfig()
        if(BuildConfig.DEBUG){
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        }
    }

    private fun configureRemoteConfig(){
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 14400
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }
}