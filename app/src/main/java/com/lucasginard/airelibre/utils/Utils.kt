package com.lucasginard.airelibre.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

object Utils {
    fun getISODate(): String {
        val calendar = Calendar.getInstance()
        var date = calendar.time
        date = Date(date.time - 60 * 60000)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val sdf2 = SimpleDateFormat("HH:mm dd-MM")
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        sdf2.timeZone = TimeZone.getTimeZone("GMT")
        SessionCache.lastUpdate = "GMT ${sdf2.format(date)}"
        return sdf.format(date)
    }
}