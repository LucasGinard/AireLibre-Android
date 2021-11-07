package com.lucasginard.airelibre.modules.home.view

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.lucasginard.airelibre.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.SphericalUtil
import com.lucasginard.airelibre.databinding.FragmentHomeBinding
import com.lucasginard.airelibre.modules.about.AboutActivity
import com.lucasginard.airelibre.modules.data.APIService
import com.lucasginard.airelibre.modules.home.domain.HomeRepository
import com.lucasginard.airelibre.modules.home.model.CityResponse
import com.lucasginard.airelibre.modules.home.viewModel.HomeViewModel
import com.lucasginard.airelibre.modules.home.viewModel.HomeViewModelFactory
import com.lucasginard.airelibre.utils.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import com.lucasginard.airelibre.utils.adapter.AdapterCityList


class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: HomeViewModel
    private lateinit var _binding: FragmentHomeBinding
    private lateinit var GoogleMap: GoogleMap
    private lateinit var lastLocation:Location
    private lateinit var adapter: AdapterCityList
    private lateinit var onSwipeTouchListener: OnSwipeTouchListener
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private var mapView: MapView? = null
    private val retrofit = APIService.getInstance()
    private var listCitys = ArrayList<CityResponse>()
    val makerLamda = fun(maker:String){
        _binding.linearInfoMarker.visibility = View.VISIBLE
        _binding.linearList.visibility = View.GONE
        _binding.linearInfoMarker.startAnimation(
            _binding.linearInfoMarker.animationCreate(
                R.anim.slide_up
            )
        )
        val cityObject = listCitys.find { it.description == maker }
        _binding.tvCiudad.text = cityObject?.description
        cityObject?.quality?.index?.let { this.textsAQI(_binding.tvDescripcion,_binding.iconInfo,_binding.tvQquality,it) }
        _binding.linearList.resizeSmall()
        _binding.rvLista.visibility = View.GONE
        _binding.btnArrow.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_arrow_up
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        requestLocation()
        configureUI()
        configureService()
        configureOnClickListeners()
        configureMaps(savedInstanceState)
        return _binding.root
    }

    private fun configureUI() {
        _binding.btnReconnect.apply {
            this.imageTintList = ContextCompat.getColorStateList(this.context , R.color.white)
        }
    }

    private fun configureAdapter(arrayList: ArrayList<CityResponse>) {
        adapter = AdapterCityList(arrayList,this,GoogleMap)
        _binding.rvLista.layoutManager = LinearLayoutManager(requireContext())
        _binding.rvLista.adapter = adapter
    }

    private fun configureOnClickListeners() {
        _binding.btnInfo.setOnClickListener {
            _binding.tvHelp.apply {
                if (this.visibility == View.GONE) {
                    this.startAnimation(this.animationCreate(R.anim.fade_in))
                    this.visibility = View.VISIBLE
                    Executors.newSingleThreadScheduledExecutor().schedule({
                        this.startAnimation(this.animationCreate(R.anim.fade_out))
                        this.visibility = View.GONE
                    }, 7, TimeUnit.SECONDS)
                }
            }
        }

        _binding.btnClose.setOnClickListener {
            _binding.linearInfoMarker.apply {
                _binding.linearInfoMarker.startAnimation(this.animationCreate(R.anim.slide_down))
                Executors.newSingleThreadScheduledExecutor().schedule({
                    visibility = View.GONE
                }, 1, TimeUnit.SECONDS)
            }
            _binding.linearList.visibility = View.VISIBLE
        }

        _binding.btnAbout.setOnClickListener {
            activity?.startActivity(
                Intent(activity, AboutActivity::class.java),
                ActivityOptions.makeCustomAnimation(activity, R.anim.fade_in, R.anim.fade_out)
                    .toBundle()
            )
        }
        onSwipeTouchListener = OnSwipeTouchListener(
            requireContext(),
            _binding.linearList,
            arrowView = _binding.btnArrow,
            viewRecycler = _binding.rvLista
        )
        onSwipeTouchListener =
            OnSwipeTouchListener(requireContext(), _binding.linearInfoMarker, _binding.linearList)

        _binding.btnArrow.setOnClickListener {
            if (_binding.rvLista.visibility == View.GONE) {
                _binding.linearList.startAnimation(_binding.linearList.animationCreate(R.anim.slide_up))
                _binding.linearList.resizeLarge()
                _binding.rvLista.visibility = View.VISIBLE
                _binding.btnArrow.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_arrow_down
                    )
                )
            } else {
                _binding.linearList.startAnimation(_binding.linearList.animationCreate(R.anim.slide_down))
                _binding.linearList.resizeSmall()
                _binding.rvLista.visibility = View.GONE
                _binding.btnArrow.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_arrow_up
                    )
                )
            }
        }

        _binding.btnReconnect.setOnClickListener {
            configureService()
        }
    }

    private fun configureService() {
        viewModel = ViewModelProvider(this, HomeViewModelFactory(HomeRepository(retrofit))).get(
            HomeViewModel::class.java
        )
        viewModel.getListCitys.observe(requireActivity(), {
            listCitys.clear()
            listCitys.addAll(it)
            configureMarkers(listCitys)
            configureAdapter(listCitys)
            calculateMarkerLocation()
            if( _binding.btnReconnect.visibility == View.VISIBLE ){
                _binding.btnReconnect.visibility = View.GONE
            }
        })
        viewModel.errorMessage.observe(requireActivity(), {
            _binding.linearList.visibility = View.GONE
            _binding.btnReconnect.visibility = View.VISIBLE
            Toast.makeText(requireContext(),getText(R.string.toastErrorNet),Toast.LENGTH_SHORT).show()
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
        //Button Center Location:
        val locationButton = (_binding.mapView.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(Integer.parseInt("2"))
        val rlp =  locationButton.layoutParams as RelativeLayout.LayoutParams
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
        rlp.setMargins(0, 160, 30, 0)
    }

    private fun configureMarkers(arrayList: ArrayList<CityResponse>) {
        for (x in arrayList) {
            if (::GoogleMap.isInitialized) {
                GoogleMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(x.latitude, x.longitude))
                        .title(x.description)
                        .icon(setImageMarker(x.quality.index))
                )
                GoogleMap.setOnMarkerClickListener { maker ->
                    makerLamda(maker.title!!)
                    true
                }
            }
        }
        if (::GoogleMap.isInitialized) {
            GoogleMap.setOnMapClickListener {
                if (_binding.linearInfoMarker.visibility == View.VISIBLE) {
                    _binding.linearInfoMarker.apply {
                        _binding.linearInfoMarker.startAnimation(this.animationCreate(R.anim.slide_down))
                        Executors.newSingleThreadScheduledExecutor().schedule({
                            visibility = View.GONE
                        }, 1, TimeUnit.SECONDS)
                    }
                    _binding.linearList.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setImageMarker(index: Int): BitmapDescriptor {
        return when (index) {
            in 0..50 -> BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_green)
            in 51..100 -> BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_yellow)
            in 101..150 -> BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_orange)
            in 151..200 -> BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_red)
            in 201..300 -> BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_purple)
            else -> BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_danger)
        }
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap.let {
            GoogleMap = it!!
        }
        val py = LatLng(-25.250, -57.536)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(py, 10f))
        googleMap?.uiSettings?.isMapToolbarEnabled = true
        updateLocation()
    }

    private fun requestLocation() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                    updateLocation()
                }
                permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                    updateLocation()
                }
                else -> {

                }
            }
        }
        locationPermissionRequest.launch(
            arrayOf(
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION
            )
        )
    }


    private fun updateLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (::GoogleMap.isInitialized) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }else{
                fusedLocationClient.lastLocation.addOnSuccessListener { position ->
                    if (position != null){
                        lastLocation = position
                        calculateMarkerLocation()
                    }
                }
                GoogleMap.isMyLocationEnabled = true
            }
        }
    }

    private fun calculateMarkerLocation(){
        if (::lastLocation.isInitialized){
            val miPos = LatLng(lastLocation.latitude,lastLocation.longitude)
            var  posicionMasCercana = LatLng(0.0, 0.0)
            var distanciaActual = Double.MAX_VALUE;

            for(x in listCitys) {
                var  distancia = SphericalUtil.computeDistanceBetween(miPos,LatLng(x.latitude,x.longitude));
                if (distanciaActual > distancia) {
                    posicionMasCercana = LatLng(x.latitude,x.longitude)
                    distanciaActual = distancia;
                }
            }
            val cerca = listCitys.find { it.latitude == posicionMasCercana.latitude && it.longitude == posicionMasCercana.longitude }
            if (cerca != null) {
                makerLamda(cerca.description)
                GoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(cerca.latitude,cerca.longitude), 13f))
            }
        }
    }
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}