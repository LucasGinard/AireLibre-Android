package com.lucasginard.airelibre.modules.home.view

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.lucasginard.airelibre.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lucasginard.airelibre.databinding.FragmentHomeBinding
import com.lucasginard.airelibre.modules.about.AboutActivity
import com.lucasginard.airelibre.modules.config.ConfigActivity
import com.lucasginard.airelibre.modules.config.ui.theme.AireLibreTheme
import com.lucasginard.airelibre.modules.data.APIService
import com.lucasginard.airelibre.modules.home.domain.HomeRepository
import com.lucasginard.airelibre.modules.home.model.CardsAQI
import com.lucasginard.airelibre.modules.home.model.CityResponse
import com.lucasginard.airelibre.modules.home.view.dialog.DialogCardsAQICompose
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
    private lateinit var adapter: AdapterCityList
    private lateinit var cityCloser: CityResponse
    private lateinit var btnArrow:ImageView

    private lateinit var GoogleMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var onSwipeTouchListener: OnSwipeTouchListener
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var booleanDialog:MutableState<Boolean>
    private var listCards = ArrayList<CardsAQI>()

    private var mapView: MapView? = null
    private val retrofit = APIService.getInstance()
    private var listCitys = ArrayList<CityResponse>()
    private var flatPermisson = false
    val makerLamda = fun(maker: String) {
        _binding.linearInfoMarker.visibility = View.VISIBLE
        _binding.linearInfoMarker.startAnimation(
            _binding.linearInfoMarker.animationCreate(
                R.anim.slide_up
            )
        )
        val cityObject = listCitys.find { it.description == maker }
        if (cityObject != null){
            _binding.tvCiudad.text = cityObject.description
            cityObject.quality.index.let {
                this.textsAQI(
                    _binding.tvDescripcion,
                    _binding.iconInfo,
                    _binding.tvQquality,
                    it
                )
            }
            GoogleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        cityObject.latitude,
                        cityObject.longitude
                    ), 13f
                )
            )
        }
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            btnArrow.background = ContextCompat.getDrawable(requireContext(),R.drawable.ic_arrow_up)
        }
        if (::cityCloser.isInitialized) {
            if (cityCloser.description == maker) {
                _binding.tvTitleCity.text = getText(R.string.tvCityCloser)
            } else _binding.tvTitleCity.text = getText(R.string.tvCity)
        } else {
            _binding.tvTitleCity.text = getText(R.string.tvCity)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        _binding.composeDialog.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AireLibreTheme(darkTheme = ThemeState.isDark) {
                    Surface(color = MaterialTheme.colors.background) {
                        booleanDialog = remember { androidx.compose.runtime.mutableStateOf(false) }
                        if (booleanDialog.value) DialogCardsAQICompose(booleanDialog,listCards)
                    }
                }
            }
        }
        requestLocation()
        configureUI()
        configureService()
        configureOnClickListeners()
        configureMaps(savedInstanceState)
        return _binding.root
    }

    private fun configureUI() {
        _binding.btnReconnect.apply {
            this.imageTintList = ContextCompat.getColorStateList(this.context, R.color.white)
        }
    }

    private fun configureAdapter(arrayList: ArrayList<CityResponse>) {
        adapter = if (::GoogleMap.isInitialized){
            AdapterCityList(arrayList, this, GoogleMap)
        }else{
            AdapterCityList(arrayList, this)
        }
        val recycler = _binding.coordinatorLayout.findViewById<RecyclerView>(R.id.rvLista)
        if (context != null){
            recycler.layoutManager = LinearLayoutManager(context)
        }
        recycler.adapter = adapter
    }

    private fun configureOnClickListeners() {
        _binding.btnInfo.setOnClickListener {
            listCards.clear()
            listCards = viewModel.getCards(requireActivity())
            booleanDialog.value = true
        }

        _binding.btnConfig.setOnClickListener {
            activity?.startActivity(
                Intent(activity, ConfigActivity::class.java)
                    .putExtra("flatPermission",flatPermisson),
                ActivityOptions.makeCustomAnimation(activity, R.anim.slide_in_right, R.anim.slide_out_left)
                    .toBundle()
            )
        }

        _binding.btnAbout.setOnClickListener {
            activity?.startActivity(
                Intent(activity, AboutActivity::class.java),
                ActivityOptions.makeCustomAnimation(activity, R.anim.slide_left, R.anim.slide_right)
                    .toBundle()
            )
        }

        _binding.btnClose.setOnClickListener {
            _binding.linearInfoMarker.apply {
                _binding.linearInfoMarker.startAnimation(this.animationCreate(R.anim.slide_down))
                Executors.newSingleThreadScheduledExecutor().schedule({
                    this.visibility = View.GONE
                }, 1, TimeUnit.SECONDS)
            }
        }


        _binding.btnReconnect.setOnClickListener {
            configureService()
        }


        onSwipeTouchListener = OnSwipeTouchListener(requireContext(), _binding.linearInfoMarker)
        bottomSheetBehavior = BottomSheetBehavior.from(_binding.coordinatorLayout.findViewById(R.id.bottomSheet))

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // handle onSlide
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    btnArrow.background = ContextCompat.getDrawable(requireContext(),R.drawable.ic_arrow_down)
                } else {
                    btnArrow.background = ContextCompat.getDrawable(requireContext(),R.drawable.ic_arrow_up)
                }
            }
        })
        btnArrow = _binding.coordinatorLayout.findViewById(R.id.btnArrow)
        btnArrow.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                btnArrow.background = ContextCompat.getDrawable(requireContext(),R.drawable.ic_arrow_up)
            } else {
                btnArrow.background = ContextCompat.getDrawable(requireContext(),R.drawable.ic_arrow_down)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            }
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
            if (_binding.btnReconnect.visibility == View.VISIBLE) {
                _binding.btnReconnect.visibility = View.GONE
                _binding.coordinatorLayout.visibility = View.VISIBLE
            }
        })
        viewModel.errorMessage.observe(requireActivity(), {
            _binding.btnReconnect.visibility = View.VISIBLE
            _binding.coordinatorLayout.visibility = View.GONE
            if(activity != null){
                Toast.makeText(requireContext(), getText(R.string.toastErrorNet), Toast.LENGTH_SHORT)
                    .show()
            }
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
        val locationButton =
            (_binding.mapView.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(
                Integer.parseInt("2")
            )
        val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams
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
        mapTheme()
        updateLocation()
    }

    private fun requestLocation() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            flatPermisson = when {
                permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                    updateLocation()
                    true
                }
                permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                    updateLocation()
                    true
                }
                else -> {
                    false
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
        if (context != null){
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
                } else {
                    fusedLocationClient.lastLocation.addOnSuccessListener { position ->
                        if (position != null) {
                            lastLocation = position
                            calculateMarkerLocation()
                        }
                    }
                    GoogleMap.isMyLocationEnabled = true
                }
            }
        }
    }

    private fun calculateMarkerLocation() {
        if (::lastLocation.isInitialized) {
            val distanceMarker = Location("Distance")
            var posicionMasCercana = LatLng(0.0, 0.0)
            var distanciaActual = Double.MAX_VALUE;

            for (x in listCitys) {
                distanceMarker.longitude = x.longitude
                distanceMarker.latitude = x.latitude
                val distancia = lastLocation.distanceTo(distanceMarker)
                x.distance = distancia/1000
                if (distanciaActual > distancia) {
                    posicionMasCercana = LatLng(x.latitude, x.longitude)
                    distanciaActual = distancia.toDouble();
                }
            }
            val cerca = listCitys.find { it.latitude == posicionMasCercana.latitude && it.longitude == posicionMasCercana.longitude }
            if (cerca != null) {
                cityCloser = cerca
                makerLamda(cerca.description)
                _binding.tvTitleCity.text = getText(R.string.tvCityCloser)
                if (::adapter.isInitialized){
                    adapter.orderList()
                }
            }
        }
    }

    private fun mapTheme(){
        if (::GoogleMap.isInitialized && context != null){
            btnArrow = _binding.coordinatorLayout.findViewById(R.id.btnArrow)
            if (!viewModel.isNotDefaultTheme()){
                ThemeState.isDark = this.getModeTheme(requireContext())
            }else{
                ThemeState.isDark = viewModel.getTheme()
            }
            if (ThemeState.isDark){
                GoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle_night))
                btnArrow.setTint(R.color.white)
            }else{
                GoogleMap.mapType = com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL
                btnArrow.setTint(R.color.black)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
        mapTheme()
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}