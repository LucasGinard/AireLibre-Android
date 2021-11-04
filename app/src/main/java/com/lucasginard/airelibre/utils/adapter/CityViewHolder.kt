package com.lucasginard.airelibre.utils.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lucasginard.airelibre.databinding.ItemCityBinding
import com.lucasginard.airelibre.modules.home.model.CityResponse

class CityViewHolder (view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemCityBinding.bind(view)

    fun bind(local: CityResponse){
        binding.tvTitleCity.text = local.description
    }
}