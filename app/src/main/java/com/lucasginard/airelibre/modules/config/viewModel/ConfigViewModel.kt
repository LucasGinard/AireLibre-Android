package com.lucasginard.airelibre.modules.config.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.config.domain.ConfigRepository
import com.lucasginard.airelibre.modules.config.model.MapData

class ConfigViewModel: ViewModel() {

    val repository = ConfigRepository()

    fun setNotIsDefaultTheme(flat:Boolean){
        repository.saveIsThemeFlat(flat)
    }

    fun setTheme(flat:Boolean){
        repository.saveThemeCustom(flat)
    }

    fun isNotDefaultTheme():Boolean{
        return repository.getIsThemeSave()
    }

    fun getTheme():Boolean{
        return repository.getThemeCustom()
    }

    fun setFlatLocationDeny(flat:Boolean){
        repository.saveNoFirstRequestLocation(flat)
    }

    fun getFlatLocationDeny():Boolean{
        return repository.getIsNotFirstRequestLocation()
    }

    fun setFlatLocationSucess(flat:Boolean){
        repository.saveFlatSucessPermissionLocation(flat)
    }

    fun getFlatLocationSucess():Boolean{
        return repository.getFlatSucessPermissionLocation()
    }

    fun setCustomMap(mapType:String){
        repository.saveMapTypeCustom(mapType)
    }

    fun getCustomMap():String{
        return repository.getMapTypeCustom()
    }

    fun getListMaps(context: Context):ArrayList<MapData>{
        val list = ArrayList<MapData>()
        list.add(MapData(R.drawable.map_default,context.getString(R.string.mapDefault)))
        list.add(MapData(R.drawable.map_satellite,context.getString(R.string.mapSatellite)))
        list.add(MapData(R.drawable.map_uber,context.getString(R.string.mapUber)))
        list.add(MapData(R.drawable.map_retro,context.getString(R.string.mapRetro)))
        list.add(MapData(R.drawable.map_light_blue,context.getString(R.string.mapLightBlue)))
        list.add(MapData(R.drawable.map_blue,context.getString(R.string.mapBlue)))
        list.add(MapData(R.drawable.map_cyber,context.getString(R.string.mapCyber)))
        list.add(MapData(R.drawable.map_fallout,context.getString(R.string.mapFallout)))
        list.add(MapData(R.drawable.map_gta,context.getString(R.string.mapGTA)))
        return list
    }


}