package com.lucasginard.airelibre.modules.home.view.adapter


import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.lucasginard.airelibre.AireLibreApp
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.databinding.ItemSensorBinding
import com.lucasginard.airelibre.modules.home.model.SensorResponse
import com.lucasginard.airelibre.modules.home.view.HomeFragment
import com.lucasginard.airelibre.modules.notifications.NotificationWorkManager
import com.lucasginard.airelibre.utils.Utils
import com.lucasginard.airelibre.utils.textAQI
import com.lucasginard.airelibre.utils.textsAQI

class SensorViewHolder (view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemSensorBinding.bind(view)

    fun bind(local: SensorResponse, fragment: HomeFragment, maps: GoogleMap?=null){
        binding.tvTitleSensor.text = local.description
        fragment.textsAQI(null, binding.stateIcon,binding.tvAQI,local.quality.index)
        configureOnClickListener(maps,local,fragment)
        validateNotificationSensor(fragment,local)

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

        binding.btnGo.setOnClickListener {
            maps?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(local.latitude,local.longitude), 13f))
            fragment.showInfoMarker(local.description)
        }

        binding.btnShare.setOnClickListener {
            val shareText = Intent(Intent.ACTION_SEND)
            shareText.type = "text/plain"
            val description: String = "".textAQI(local.quality.index, fragment.requireContext())
            val dataToShare = "Sensor: ${binding.tvTitleSensor.text}.\nEstado del aire: ${binding.stateIcon.text} - AQI: ${binding.tvAQI.text}\nDescripción:\n$description\nDatos obtenidos en www.airelib.re"
            shareText.putExtra(Intent.EXTRA_TEXT, dataToShare)
            fragment.activity?.startActivity(Intent.createChooser(shareText, null))
        }

        binding.btnNotify.setOnClickListener {
            if (local.isEnableNotification){
                fragment.context?.let { context ->
                    Utils.showDialog(context,fragment.getString(R.string.titleDisableNotification)){
                        AireLibreApp.prefs.cancelScheduledNotification(local.description)
                        val notification = NotificationWorkManager(context)
                        notification.cancelPeriodicWork(local.description)
                        notification.cancelOneTimeNotification(local.description)

                        fragment.viewModel.sensorNotify = local
                        fragment.updateAdapterItem(false)
                    }
                }
            }else{
                fragment.showDialogConfigure(local)
            }
        }
    }

    private fun validateNotificationSensor(fragment: HomeFragment,sensorResponse: SensorResponse){
        if (sensorResponse.isEnableNotification){
            binding.btnNotify.text = fragment.getString(R.string.tvNotifyEnable)
            binding.btnNotify.setIconResource(R.drawable.ic_notify_active)
        }else{
            binding.btnNotify.text = fragment.getString(R.string.tvNotify)
            binding.btnNotify.setIconResource(R.drawable.ic_notify_add)
        }
    }
}