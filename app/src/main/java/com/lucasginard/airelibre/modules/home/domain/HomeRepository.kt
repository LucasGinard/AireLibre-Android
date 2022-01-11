package com.lucasginard.airelibre.modules.home.domain

import com.lucasginard.airelibre.AireLibreApp
import com.lucasginard.airelibre.modules.data.APIService

class HomeRepository constructor(private val retrofitService: APIService) {

    fun getAllCitys() = retrofitService.getList()

    fun getIsThemeSave():Boolean{
        return AireLibreApp.prefs.flatTheme
    }

    fun getThemeCustom():Boolean{
        return AireLibreApp.prefs.themeCustom
    }
}