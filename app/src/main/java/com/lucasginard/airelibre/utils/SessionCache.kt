package com.lucasginard.airelibre.utils

import android.content.Context
import com.lucasginard.airelibre.modules.about.model.Contributor
import com.lucasginard.airelibre.modules.about.model.LinksDynamic
import com.lucasginard.airelibre.modules.notifications.NotificationManager

open class SessionCache {
    companion object{
        var listContributorsCache = ArrayList<Contributor>()
        var linksDynamicCache:LinksDynamic ?= null
        var lastUpdate:String = ""

        fun getAllNotificationEnable(context: Context):ArrayList<Int>{
            return NotificationManager(context).getAllNotificationsEnables()
        }
    }
}