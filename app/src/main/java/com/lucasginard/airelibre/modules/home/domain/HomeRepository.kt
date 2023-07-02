package com.lucasginard.airelibre.modules.home.domain

import com.lucasginard.airelibre.AireLibreApp
import com.lucasginard.airelibre.modules.data.APIHome
import com.lucasginard.airelibre.utils.Utils
import javax.inject.Inject

class HomeRepository @Inject constructor(private val retrofitService: APIHome) {

    fun getAllSensors() = retrofitService.getList(Utils.getISODate())

    fun getStatus() = retrofitService.getStatus()

    fun getIsThemeSave():Boolean{
        return AireLibreApp.prefs.flatTheme
    }

    fun getThemeCustom():Boolean{
        return AireLibreApp.prefs.themeCustom
    }

    fun getStyleMap():String{
        return AireLibreApp.prefs.customMap
    }

    fun getListScheduledNotifications():Set<String>{
        return AireLibreApp.prefs.getAllScheduledNotifications()
    }
}