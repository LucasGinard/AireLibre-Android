package com.lucasginard.airelibre.utils.adapter

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.lucasginard.airelibre.databinding.ItemSensorBinding
import com.lucasginard.airelibre.modules.home.model.SensorResponse
import com.lucasginard.airelibre.modules.home.view.HomeFragment
import com.lucasginard.airelibre.utils.textAQI
import com.lucasginard.airelibre.utils.textsAQI

class SensorViewHolder (view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemSensorBinding.bind(view)

    fun bind(local: SensorResponse, fragment: HomeFragment, maps: GoogleMap?=null){
        binding.tvTitleSensor.text = local.description
        fragment.textsAQI(null, binding.stateIcon,binding.tvAQI,local.quality.index)
        configureOnClickListener(maps,local,fragment)

        if (local.distance == 0.0F || local.distance == null){
            binding.tvDistance.visibility = View.INVISIBLE
            binding.tvSensorTitle.visibility = View.INVISIBLE
        }else{
            binding.tvDistance.visibility = View.VISIBLE
            binding.tvSensorTitle.visibility = View.VISIBLE
            val distanceType:String
            val distance = if (local.distance!! < 1F){
                val converMeter = local.distance!! * 1000
                distanceType ="metros"
                converMeter.toString().substringBefore(".")
            }else{
                distanceType ="km"
                local.distance.toString().substringBefore(".")
            }
            binding.tvDistance.text = "$distance $distanceType"
        }
    }

    private fun configureOnClickListener(maps: GoogleMap?, local: SensorResponse, fragment: HomeFragment) {
        binding.btnInfoAQI.setOnClickListener {
            fragment.infoAQI()
        }

        binding.tvLink.setOnClickListener {
            maps?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(local.latitude,local.longitude), 13f))
            fragment.showInfoMarker(local.description)
        }

        binding.tvShare.setOnClickListener {
            val shareText = Intent(Intent.ACTION_SEND)
            shareText.type = "text/plain"
            val description: String = "".textAQI(local.quality.index, fragment.requireContext())
            val dataToShare = "Sensor: ${binding.tvTitleSensor.text}.\nEstado del aire: ${binding.stateIcon.text} - AQI: ${binding.tvAQI.text}\nDescripción:\n$description\nDatos obtenidos en www.airelib.re"
            shareText.putExtra(Intent.EXTRA_TEXT, dataToShare)
            fragment.activity?.startActivity(Intent.createChooser(shareText, null))
        }

        binding.tvNotify.setOnClickListener {

        }
    }
}