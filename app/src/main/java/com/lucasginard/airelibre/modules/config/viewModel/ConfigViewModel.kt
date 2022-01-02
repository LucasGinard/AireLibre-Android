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
}