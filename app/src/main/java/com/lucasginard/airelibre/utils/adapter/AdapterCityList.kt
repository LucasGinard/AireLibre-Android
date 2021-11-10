package com.lucasginard.airelibre.utils.adapter

import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.home.model.CityResponse
import com.lucasginard.airelibre.modules.home.view.HomeFragment
import java.text.DecimalFormat

class AdapterCityList(var cityList: ArrayList<CityResponse>,val fragment: HomeFragment,val maps: GoogleMap,var lastLocation: Location?=null) : RecyclerView.Adapter<CityViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CityViewHolder(layoutInflater.inflate(R.layout.item_city, parent, false))
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val item = cityList[position]
        val distance = calculateDistance(LatLng(item.latitude,item.longitude),lastLocation)
        holder.bind(item,fragment,maps,distance)
    }

    override fun getItemCount(): Int = cityList.size

    fun calculateDistance(Maker: LatLng, lastLocation: Location?=null):String{
        return if (lastLocation != null){
            val deciF =  DecimalFormat("#,###")
            val locationMaker = Location("Maker")
            locationMaker.latitude = Maker.latitude
            locationMaker.longitude = Maker.longitude
            val distance = lastLocation.distanceTo(locationMaker)
            deciF.format(distance).substring(0,3)
        }else{
            ""
        }
    }
}