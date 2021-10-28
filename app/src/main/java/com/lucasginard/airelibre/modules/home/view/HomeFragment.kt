package com.lucasginard.airelibre.modules.home.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
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

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap.let {
            GoogleMap = it!!
        }
        val py = LatLng(-25.294589, -57.578563)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(py, 6f))
        googleMap?.uiSettings?.isMapToolbarEnabled = false
    }

    companion object {
        fun newInstance()=HomeFragment()
    }
}