package com.lucasginard.airelibre.utils.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.lucasginard.airelibre.databinding.ItemCityBinding
import com.lucasginard.airelibre.modules.home.model.CityResponse
import com.lucasginard.airelibre.modules.home.view.HomeFragment
import com.lucasginard.airelibre.utils.textsAQI

class CityViewHolder (view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemCityBinding.bind(view)

    fun bind(local: CityResponse, fragment: HomeFragment, maps: GoogleMap){
        binding.tvTitleCity.text = local.description
        fragment.textsAQI(null, binding.stateIcon,binding.tvAQI,local.quality.index)
        binding.tvLink.setOnClickListener {
            maps.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(local.latitude,local.longitude), 13f))
            fragment.makerLamda(local.description)
        }
        if (local.distance == 0.0F || local.distance == null){
            binding.tvDistance.visibility = View.INVISIBLE
            binding.tvSensorTitle.visibility = View.INVISIBLE
        }else{
            val distance = local.distance.toString().substringBefore(".")
            binding.tvDistance.text = "$distance Km"
        }
    }
}