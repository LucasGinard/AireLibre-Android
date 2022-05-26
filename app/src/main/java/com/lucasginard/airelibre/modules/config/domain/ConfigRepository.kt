package com.lucasginard.airelibre.modules.config.domain

import com.lucasginard.airelibre.AireLibreApp

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

    fun getIsNotFirstRequestLocation():Boolean{
        return AireLibreApp.prefs.flatDenyPermissonLocation
    }

    fun saveNoFirstRequestLocation(flat:Boolean){
        AireLibreApp.prefs.flatDenyPermissonLocation = flat
    }

    fun saveFlatSucessPermissionLocation(flat:Boolean){
        AireLibreApp.prefs.flatAcceptPermissonLocation = flat
    }

    fun getFlatSucessPermissionLocation():Boolean{
        return AireLibreApp.prefs.flatAcceptPermissonLocation
    }

    fun saveMapTypeCustom(mapType:String){
        AireLibreApp.prefs.customMap = mapType
    }


}