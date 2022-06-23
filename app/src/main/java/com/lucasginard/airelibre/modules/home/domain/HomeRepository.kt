package com.lucasginard.airelibre.modules.home.domain

import com.lucasginard.airelibre.AireLibreApp
import com.lucasginard.airelibre.modules.data.APIHome
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class HomeRepository @Inject constructor(private val retrofitService: APIHome) {

    fun getAllCitys() = retrofitService.getList(getISODate())

    fun getIsThemeSave():Boolean{
        return AireLibreApp.prefs.flatTheme
    }

    fun getThemeCustom():Boolean{
        return AireLibreApp.prefs.themeCustom
    }

    fun getStyleMap():String{
        return AireLibreApp.prefs.customMap
    }

    fun getISODate(): String {
        val calendar = Calendar.getInstance()
        var date = calendar.time
        date = Date(date.time - 60 * 60000)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        return sdf.format(date)
    }
}