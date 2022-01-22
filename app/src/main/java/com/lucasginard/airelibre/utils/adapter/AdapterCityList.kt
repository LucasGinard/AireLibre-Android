package com.lucasginard.airelibre.utils.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.home.model.CityResponse
import com.lucasginard.airelibre.modules.home.view.HomeFragment

class AdapterCityList(var cityList: ArrayList<CityResponse>,val fragment: HomeFragment,val maps: GoogleMap?=null) : RecyclerView.Adapter<CityViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CityViewHolder(layoutInflater.inflate(R.layout.item_city, parent, false))
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val item = cityList[position]
        if (maps != null) {
            holder.bind(item,fragment,maps)
        }else{
            holder.bind(item,fragment)
        }
    }

    override fun getItemCount(): Int = cityList.size

    private fun orderListDown(orderName:String){
        val aux = when(orderName){
            "Distance" ->{
                cityList.sortedBy { it.distance }
            }
            "AQI"->{
                cityList.sortedBy { it.quality.index }
            }
            else -> cityList
        }
        cityList.clear()
        cityList.addAll(aux)
        notifyDataSetChanged()
    }

    private fun orderListUp(orderName: String){
        val aux = when(orderName){
            "Distance" ->{
                cityList.sortedByDescending { it.distance }
            }
            "AQI"->{
                cityList.sortedByDescending { it.quality.index }
            }
            else -> cityList
        }
        cityList.clear()
        cityList.addAll(aux)
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