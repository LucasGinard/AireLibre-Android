package com.lucasginard.airelibre.modules.data

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Context) {
    val PREFS_NAME = "airelibre.sharedpreferences"
    val SHARED_FLAT = "theme_flat"
    val SHARED_THEME = "custom_theme"
    val SHARED_LOCATION_DENY = "location_deny"
    val SHARED_LOCATION_ACCEPT = "location_accept"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)

    var flatTheme: Boolean
        get() = prefs.getBoolean(SHARED_FLAT, false)
        set(value) = prefs.edit().putBoolean(SHARED_FLAT, value).apply()

    var themeCustom: Boolean
        get() = prefs.getBoolean(SHARED_THEME, false)
        set(value) = prefs.edit().putBoolean(SHARED_THEME, value).apply()

    var flatDenyPermissonLocation: Boolean
        get() = prefs.getBoolean(SHARED_LOCATION_DENY, false)
        set(value) = prefs.edit().putBoolean(SHARED_LOCATION_DENY, value).apply()

    var flatAcceptPermissonLocation: Boolean
        get() = prefs.getBoolean(SHARED_LOCATION_ACCEPT, false)
        set(value) = prefs.edit().putBoolean(SHARED_LOCATION_ACCEPT, value).apply()
}