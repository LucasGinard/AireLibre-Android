package com.lucasginard.airelibre.modules.home.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.home.model.SensorResponse
import com.lucasginard.airelibre.modules.home.view.HomeFragment

class AdapterSensorList(var sensorList: ArrayList<SensorResponse>, val fragment: HomeFragment, val maps: GoogleMap?=null) : RecyclerView.Adapter<SensorViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SensorViewHolder(layoutInflater.inflate(R.layout.item_sensor, parent, false))
    }

    override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
        val item = sensorList[position]
        if (maps != null) {
            holder.bind(item,fragment,maps)
        }else{
            holder.bind(item,fragment)
        }
    }

    override fun getItemCount(): Int = sensorList.size

    private fun orderListDown(orderName:String){
        val aux = when(orderName){
            "Distance" ->{
                sensorList.sortedBy { it.distance }
            }
            "AQI"->{
                sensorList.sortedBy { it.quality.index }
            }
            "NotifyFilter"->{
                sensorList.sortedWith(compareByDescending { it.isEnableNotification })
            }
            else -> sensorList
        }
        sensorList.clear()
        sensorList.addAll(aux)
        notifyDataSetChanged()
    }

    private fun orderListUp(orderName: String){
        val aux = when(orderName){
            "Distance" ->{
                sensorList.sortedByDescending { it.distance }
            }
            "AQI"->{
                sensorList.sortedByDescending { it.quality.index }
            }
            "NotifyFilter"->{
                sensorList.sortedWith(compareBy { it.isEnableNotification })
            }
            else -> sensorList
        }
        sensorList.clear()
        sensorList.addAll(aux)
        notifyDataSetChanged()
    }

    fun orderList(orderName:String,isDown:Boolean){
        if (isDown){
            orderListDown(orderName)
        }else{
            orderListUp(orderName)
        }
    }
}