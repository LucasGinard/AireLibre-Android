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

    fun orderList(){
        val aux = cityList.sortedBy { it.distance }
        cityList.clear()
        cityList.addAll(aux)
        notifyDataSetChanged()
    }
}