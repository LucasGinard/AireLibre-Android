package com.lucasginard.airelibre.modules.data

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Context) {
    private val PREFS_NAME = "airelibre.sharedpreferences"
    private val SHARED_FLAT = "theme_flat"
    private val SHARED_THEME = "custom_theme"
    private val SHARED_LOCATION_DENY = "location_deny"
    private val SHARED_LOCATION_ACCEPT = "location_accept"
    private val SHARED_MAP_CUSTOM = "map_custom"
    private val SHARED_NOTIFICATION_IDS = "notification_ids"

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

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

    var customMap: String
        get() = prefs.getString(SHARED_MAP_CUSTOM, "Pred") ?: "Pred"
        set(value) = prefs.edit().putString(SHARED_MAP_CUSTOM, value).apply()

    fun scheduleNotification(notificationId: String) {
        val notificationIds = prefs.getStringSet(SHARED_NOTIFICATION_IDS, setOf())?.toMutableSet() ?: mutableSetOf()
        notificationIds.add(notificationId)
        prefs.edit().putStringSet(SHARED_NOTIFICATION_IDS, notificationIds).apply()
    }

    fun cancelScheduledNotification(notificationId: String) {
        val notificationIds = prefs.getStringSet(SHARED_NOTIFICATION_IDS, setOf())?.toMutableSet() ?: mutableSetOf()
        notificationIds.remove(notificationId)
        prefs.edit().putStringSet(SHARED_NOTIFICATION_IDS, notificationIds).apply()
    }

    fun getAllScheduledNotifications(): Set<String> {
        return prefs.getStringSet(SHARED_NOTIFICATION_IDS, setOf()) ?: setOf()
    }
}