package com.lucasginard.airelibre.utils.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.home.model.CityResponse
import com.lucasginard.airelibre.modules.home.view.HomeFragment

class AdapterCityList(var cityList: ArrayList<CityResponse>,val fragment: HomeFragment,val maps: GoogleMap) : RecyclerView.Adapter<CityViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CityViewHolder(layoutInflater.inflate(R.layout.item_city, parent, false))
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val item = cityList[position]
        holder.bind(item,fragment,maps)
    }

    override fun getItemCount(): Int = cityList.size
}