package com.lucasginard.airelibre.modules.home.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.lucasginard.airelibre.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.lucasginard.airelibre.databinding.FragmentHomeBinding
import com.lucasginard.airelibre.modules.data.APIService
import com.lucasginard.airelibre.modules.home.domain.HomeRepository
import com.lucasginard.airelibre.modules.home.model.CityListResponse
import com.lucasginard.airelibre.modules.home.viewModel.HomeViewModel
import com.lucasginard.airelibre.modules.home.viewModel.HomeViewModelFactory

class HomeFragment : Fragment(), OnMapReadyCallback {


    private lateinit var viewModel:HomeViewModel
    private lateinit var _binding:FragmentHomeBinding
    private lateinit var GoogleMap: GoogleMap

    private var mapView: MapView? = null
    private val retrofit = APIService.getInstance()
    private var listCitys = ArrayList<CityListResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        configureMaps(savedInstanceState)
        configureService()

        return _binding.root
    }

    private fun configureService() {
        viewModel = ViewModelProvider(this,HomeViewModelFactory(HomeRepository(retrofit))).get(HomeViewModel::class.java)
        viewModel.getListCitys.observe(requireActivity(), {
            listCitys.clear()
            listCitys.addAll(it)
            configureMarkers(listCitys)
        })
        viewModel.errorMessage.observe(requireActivity(), {
            Log.d("testArray","error")
        })
        viewModel.getAllCity()

    }

    private fun configureMaps(saved: Bundle?) {
        mapView = _binding.mapView
        mapView?.onCreate(saved)
        mapView?.onResume()
        try {
            MapsInitializer.initialize(requireActivity().applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mapView?.getMapAsync(this)
    }

    private fun configureMarkers(arrayList: ArrayList<CityListResponse>) {
        for (x in arrayList){
            GoogleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(x.latitude,x.longitude))
                    .title(x.description)
                    .icon(setImageMarker(x.quality.index))
            )
        }
    }

    private fun setImageMarker(index:Int): BitmapDescriptor {
        when (index) {
            in 0..50 -> return BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_green)
            in 51..100 -> return BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_yellow)
            in 101..150 -> return BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_orange)
            in 151..200 -> return BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_red)
            in 201..300 -> return BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_purple)
            else -> return BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_danger)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap.let {
            GoogleMap = it!!
        }
        val py = LatLng(-25.250, -57.536)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(py, 10f))
        googleMap?.uiSettings?.isMapToolbarEnabled = false
    }

    companion object {
        fun newInstance()=HomeFragment()
    }
}