package com.lucasginard.airelibre.modules.config.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lucasginard.airelibre.modules.config.domain.ConfigRepository

class ConfigViewModelFactory constructor(private val repository: ConfigRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ConfigViewModel::class.java)) {
            ConfigViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
