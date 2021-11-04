package com.lucasginard.airelibre.utils.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.home.model.CityResponse

class AdapterCityList(var cityList: ArrayList<CityResponse>) : RecyclerView.Adapter<CityViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CityViewHolder(layoutInflater.inflate(R.layout.item_city, parent, false))
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val item = cityList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = cityList.size
}