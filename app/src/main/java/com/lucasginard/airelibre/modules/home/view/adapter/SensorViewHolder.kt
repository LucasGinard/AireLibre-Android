package com.lucasginard.airelibre.modules.home.view.adapter


import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.lucasginard.airelibre.AireLibreApp
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.databinding.ItemSensorBinding
import com.lucasginard.airelibre.modules.home.model.SensorResponse
import com.lucasginard.airelibre.modules.home.view.HomeFragment
import com.lucasginard.airelibre.utils.SessionCache
import com.lucasginard.airelibre.utils.hexToInt
import com.lucasginard.airelibre.utils.setUnderlineText
import com.lucasginard.airelibre.utils.textAQI
import com.lucasginard.airelibre.utils.textsAQI

class SensorViewHolder (view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemSensorBinding.bind(view)

    fun bind(local: SensorResponse, fragment: HomeFragment, maps: GoogleMap?=null){
        local.isEnableNotification = fragment.viewModel.isActiveScheduleAlarmSensor(local.source.hexToInt().toString())

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
            if (local.isEnableNotification){
                AireLibreApp.prefs.cancelScheduledNotification(local.source.hexToInt().toString())
                val notificationManager = fragment.requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                notificationManager.cancel(local.source.hexToInt())
                fragment.updateAdapterItem()
            }else{
                fragment.showDialogConfigure(local)
            }
        }
    }

    private fun validateNotificationSensor(fragment: HomeFragment,sensorResponse: SensorResponse){
        if (sensorResponse.isEnableNotification){
            val textNotifyEnable = fragment.getString(R.string.tvNotifyEnable)
            binding.tvNotify.setUnderlineText(textNotifyEnable)

            fragment.context?.let {
                binding.imNotify.setImageDrawable(ContextCompat.getDrawable(it,R.drawable.ic_notify_active))
            }
        }else{
            val textNotifyDisable = fragment.getString(R.string.tvNotify)
            binding.tvNotify.setUnderlineText(textNotifyDisable)

            fragment.context?.let {
                binding.imNotify.setImageDrawable(ContextCompat.getDrawable(it,R.drawable.ic_notify_add))
            }
        }
    }
}