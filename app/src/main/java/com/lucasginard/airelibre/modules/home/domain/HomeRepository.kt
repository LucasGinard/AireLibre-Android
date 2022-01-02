package com.lucasginard.airelibre.modules.home.domain

import com.lucasginard.airelibre.modules.AireLibreApp
import com.lucasginard.airelibre.modules.data.APIService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeRepository constructor(private val retrofitService: APIService) {

    fun getAllCitys() = retrofitService.getList()

    fun getIsThemeSave():Boolean{
        return AireLibreApp.prefs.flatTheme
    }

    fun getThemeCustom():Boolean{
        return AireLibreApp.prefs.themeCustom
    }
}