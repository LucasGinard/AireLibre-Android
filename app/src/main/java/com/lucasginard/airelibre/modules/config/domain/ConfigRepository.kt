package com.lucasginard.airelibre.modules.config.domain

import com.lucasginard.airelibre.modules.AireLibreApp

class ConfigRepository {

    fun saveThemeCustom(themeMode:Boolean){
        AireLibreApp.prefs.themeCustom = themeMode
    }

    fun getThemeCustom():Boolean{
        return AireLibreApp.prefs.themeCustom
    }

    fun getIsThemeSave():Boolean{
        return AireLibreApp.prefs.flatTheme
    }

    fun saveIsThemeFlat(flatTheme:Boolean){
        AireLibreApp.prefs.flatTheme = flatTheme
    }
}