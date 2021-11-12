package com.lucasginard.airelibre.utils.adapter

import android.location.Location
import android.util.Log
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
        item.distance = distance
        holder.bind(item,fragment,maps)

    }

    override fun getItemCount(): Int = cityList.size

    private fun calculateDistance(Maker: LatLng, lastLocation: Location?=null):Float{
        return if (lastLocation != null){
            val locationMaker = Location("Maker")
            locationMaker.latitude = Maker.latitude
            locationMaker.longitude = Maker.longitude
            lastLocation.distanceTo(locationMaker)/1000
        }else{
            0.0F
        }
    }

    fun orderList(){
        val aux = cityList.sortedBy { it.distance }
        Log.d("testOrder", cityList[0].distance.toString())
        cityList.clear()
        cityList.addAll(aux)
        Log.d("testOrder","Ordenado: "+ cityList[0].distance.toString())
        notifyDataSetChanged()
    }
}