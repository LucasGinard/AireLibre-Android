package com.lucasginard.airelibre.modules.config.viewModel

import androidx.lifecycle.ViewModel
import com.lucasginard.airelibre.modules.config.domain.ConfigRepository

class ConfigViewModel constructor(private val repository: ConfigRepository) : ViewModel() {

    fun setFlatTheme(flat:Boolean){
        repository.saveIsThemeFlat(flat)
    }

    fun setTheme(flat:Boolean){
        repository.saveThemeCustom(flat)
    }

    fun getFlatTheme():Boolean{
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
}