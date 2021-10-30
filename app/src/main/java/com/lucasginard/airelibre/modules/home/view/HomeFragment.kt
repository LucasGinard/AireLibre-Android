package com.lucasginard.airelibre.modules.home.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.lucasginard.airelibre.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.lucasginard.airelibre.databinding.FragmentHomeBinding
import com.lucasginard.airelibre.modules.about.AboutActivity
import com.lucasginard.airelibre.modules.data.APIService
import com.lucasginard.airelibre.modules.home.domain.HomeRepository
import com.lucasginard.airelibre.modules.home.model.CityResponse
import com.lucasginard.airelibre.modules.home.viewModel.HomeViewModel
import com.lucasginard.airelibre.modules.home.viewModel.HomeViewModelFactory
import com.lucasginard.airelibre.utils.animationCreate
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), OnMapReadyCallback {


    private lateinit var viewModel:HomeViewModel
    private lateinit var _binding:FragmentHomeBinding
    private lateinit var GoogleMap: GoogleMap

    private var mapView: MapView? = null
    private val retrofit = APIService.getInstance()
    private var listCitys = ArrayList<CityResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        configureMaps(savedInstanceState)
        configureService()
        configureOnListeners()
        return _binding.root
    }

    private fun configureOnListeners() {
        _binding.btnInfo.setOnClickListener {
            _binding.tvHelp.apply {
                if (this.visibility == View.GONE){
                    this.startAnimation(this.animationCreate(R.anim.fade_in))
                    this.visibility = View.VISIBLE
                    Executors.newSingleThreadScheduledExecutor().schedule({
                        this.startAnimation(this.animationCreate(R.anim.fade_out))
                        this.visibility = View.GONE
                    }, 7, TimeUnit.SECONDS)
                }
            }
        }

        _binding.btnAbout.setOnClickListener {
            activity?.startActivity(Intent(activity,AboutActivity::class.java))
        }
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

    private fun configureMarkers(arrayList: ArrayList<CityResponse>) {
        for (x in arrayList){
            if(::GoogleMap.isInitialized){
                GoogleMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(x.latitude,x.longitude))
                        .title(x.description)
                        .icon(setImageMarker(x.quality.index))
                )
                GoogleMap.setOnMarkerClickListener { maker ->
                    _binding.linearInfoMarker.visibility = View.VISIBLE
                    _binding.linearInfoMarker.startAnimation(_binding.linearInfoMarker.animationCreate(R.anim.slide_up))
                    val cityObject = arrayList.find { it.description == maker.title}
                    _binding.tvCiudad.text = cityObject?.description
                    cityObject?.quality?.index?.let { setInfoMarker(it)}
                    true
                }
            }
        }
        if(::GoogleMap.isInitialized){
            GoogleMap.setOnMapClickListener {
                if (_binding.linearInfoMarker.visibility == View.VISIBLE){
                    _binding.linearInfoMarker.apply {
                        _binding.linearInfoMarker.startAnimation(this.animationCreate(R.anim.slide_down))
                        Executors.newSingleThreadScheduledExecutor().schedule({
                            visibility = View.GONE
                        }, 1, TimeUnit.SECONDS)

                    }
                }
            }
        }
    }

    private fun setImageMarker(index:Int): BitmapDescriptor {
        return when (index) {
            in 0..50 -> BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_green)
            in 51..100 -> BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_yellow)
            in 101..150 -> BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_orange)
            in 151..200 -> BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_red)
            in 201..300 -> BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_purple)
            else -> BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_danger)
        }
    }

    private fun setInfoMarker(index:Int){
        when (index) {
            in 0..50 -> {
                _binding.iconInfo.text = "\uD83D\uDFE2\uD83D\uDC4D"
                _binding.tvDescripcion.text = "Escaso riesgo de contaminación atmosférica, calidad de aire satisfactoria."
            }
            in 51..100 -> {
                _binding.iconInfo.text = "\uD83D\uDFE1\uD83D\uDE10"
                _binding.tvDescripcion.text = "Calidad de aire aceptable, riesgo moderado para la salud de personas sensibles"
            }
            in 101..150 -> {
                _binding.iconInfo.text = "\uD83D\uDFE0⚠\uD83D\uDE37"
                _binding.tvDescripcion.text = "Insalubre para personas sensibles."
            }
            in 151..200 -> {
                _binding.iconInfo.text = "\uD83D\uDD34⚠\uD83D\uDE37"
                _binding.tvDescripcion.text = "Riesgo general para las personas, efectos más graves en personas sensibles."
            }
            in 201..300 -> {
                _binding.iconInfo.text = "\uD83D\uDFE3☣️☣"
                _binding.tvDescripcion.text = "Condición de emergencia."
            }
            else -> {
                _binding.iconInfo.text = "\uD83D\uDFE4☠️☠"
                _binding.tvDescripcion.text = "Alerta sanitaria, efectos graves para toda la población."
            }
        }
        _binding.tvQquality.text = index.toString()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap.let {
            GoogleMap = it!!
        }
        val py = LatLng(-25.250, -57.536)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(py, 10f))
        googleMap?.uiSettings?.isMapToolbarEnabled = false
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    companion object {
        fun newInstance()=HomeFragment()
    }
}