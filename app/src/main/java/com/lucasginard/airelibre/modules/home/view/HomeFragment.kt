package com.lucasginard.airelibre.modules.home.view

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.compose.material.*
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.databinding.FragmentHomeBinding
import com.lucasginard.airelibre.modules.config.ui.theme.AireLibreTheme
import com.lucasginard.airelibre.modules.data.APIService
import com.lucasginard.airelibre.modules.home.domain.HomeRepository
import com.lucasginard.airelibre.modules.home.model.CardsAQI
import com.lucasginard.airelibre.modules.home.model.CityResponse
import com.lucasginard.airelibre.modules.home.view.dialog.DialogCardsAQICompose
import com.lucasginard.airelibre.modules.home.viewModel.HomeViewModel
import com.lucasginard.airelibre.modules.home.viewModel.HomeViewModelFactory
import com.lucasginard.airelibre.utils.*
import com.lucasginard.airelibre.utils.adapter.AdapterCityList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: HomeViewModel
    private lateinit var _binding: FragmentHomeBinding
    private lateinit var adapter: AdapterCityList
    private lateinit var cityCloser: CityResponse
    private lateinit var btnArrow:ImageView
    private lateinit var btnOrderList:ImageView
    private lateinit var recycler:RecyclerView
    private lateinit var btnFilter:ImageButton
    private lateinit var btnRefresh:ImageButton
    private lateinit var tvFilter:TextView
    private lateinit var filterAdapter:String

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
    private var isDown = true
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
                _binding.tvTitleCity.text = context?.getText(R.string.tvSensorCloser) ?: "Sensor más cercano:"
            } else _binding.tvTitleCity.text = context?.getText(R.string.tvSensor) ?: "Sensor:"
        } else {
            _binding.tvTitleCity.text = context?.getText(R.string.tvSensor) ?: "Sensor:"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        _binding.layoutCompose.apply {
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
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureUI()
        configureAdapter()
        configureService()
        configureOnClickListeners()
        configureMaps(savedInstanceState)
    }

    private fun configureUI() {
        _binding.btnReconnect.apply {
            this.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }
        filterAdapter = context?.getString(R.string.itemDistance) ?: "Distance"
        btnOrderList = _binding.coordinatorLayout.findViewById(R.id.btnOrder)
        recycler = _binding.coordinatorLayout.findViewById(R.id.rvLista)
        btnFilter = _binding.coordinatorLayout.findViewById(R.id.btnFilter)
        tvFilter = _binding.coordinatorLayout.findViewById(R.id.tvFilter)
        btnRefresh = _binding.coordinatorLayout.findViewById(R.id.btnRefreshList)
    }

    private fun configureAdapter() {
        adapter = if (::GoogleMap.isInitialized){
            AdapterCityList(listCitys, this, GoogleMap)
        }else{
            AdapterCityList(listCitys, this)
        }
        if (context != null){
            recycler.layoutManager = LinearLayoutManager(context)
        }
        recycler.adapter = adapter
        if (::adapter.isInitialized && !flatPermisson){
            filterAdapter = context?.getString(R.string.itemAQI) ?: "AQI"
            isDown = true
            orderList()
            tvFilter.text = filterAdapter
            btnFilter.visibility = View.GONE
        }
    }

    private fun configureOnClickListeners() {
        _binding.btnInfo.setOnClickListener {
            listCards.clear()
            listCards = viewModel.getCards(requireActivity())
            booleanDialog.value = true
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
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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

        btnOrderList.setOnClickListener {
            if (btnOrderList.rotation == 90f){
                btnOrderList.animationArrow(-90f)
                isDown = true
                orderList()
            } else{
                btnOrderList.animationArrow(90f)
                isDown = false
                orderList()
            }
        }

        btnFilter.setOnClickListener { v: View ->
            showItemsFilter(v, R.menu.popup_menu_filter)
        }

        btnRefresh.setOnClickListener {
            btnRefresh.animationRefresh()
            Handler(Looper.getMainLooper()).postDelayed({
                viewModel.getAllCity()
            }, 100)
        }
    }

    private fun showItemsFilter(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.title){
                getText(R.string.tvDistance) -> {
                    filterAdapter = context?.getString(R.string.itemDistance) ?: "Distance"
                    tvFilter.text = getText(R.string.tvDistance)
                    orderList()
                    true
                }
                getText(R.string.itemAQI) -> {
                    filterAdapter = context?.getString(R.string.itemAQI) ?: "AQI"
                    tvFilter.text = filterAdapter
                    orderList()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun configureService() {
        viewModel = ViewModelProvider(this, HomeViewModelFactory(HomeRepository(retrofit))).get(
            HomeViewModel::class.java
        )
        viewModel.getListCitys.observe(requireActivity()) {
            if (it != null){
                listCitys.clear()
                listCitys.addAll(it)
                adapter.notifyDataSetChanged()
                configureMarkers(listCitys)
                calculateMarkerLocation()
                if (_binding.btnReconnect.visibility == View.VISIBLE) {
                    _binding.btnReconnect.visibility = View.GONE
                    _binding.coordinatorLayout.visibility = View.VISIBLE
                }
            }else {
                errorConnectService()
            }

        }
        viewModel.errorMessage.observe(requireActivity()) {
            errorConnectService()
        }
        viewModel.getAllCity()
    }

    private fun errorConnectService(){
        if (listCitys.isNotEmpty()){
            this.ToastCustom(context?.getString(R.string.toastErrorRetry) ?: "Hubo un problema intente de nuevo")
        }else{
            _binding.btnReconnect.visibility = View.VISIBLE
            _binding.coordinatorLayout.visibility = View.GONE
            if (activity != null) {
                this.ToastCustom(context?.getString(R.string.toastErrorNet) ?: "Sin conexión compruebe su conexión")
            }
        }
    }

    private fun configureMaps(saved: Bundle?) {
        mapView = _binding.mapViewFragment
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
            (_binding.mapViewFragment.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(
                Integer.parseInt("2")
            )
        val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
        rlp.setMargins(0, 160, 30, 0)
    }

    private fun configureMarkers(arrayList: ArrayList<CityResponse>) {
        for (x in arrayList) {
            if (::GoogleMap.isInitialized && mapView != null) {
                GoogleMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(x.latitude, x.longitude))
                        .title(x.description)
                        .icon(setMarkerIcon(x))
                )
                GoogleMap.setOnMarkerClickListener { maker ->
                    makerLamda(maker.title!!)
                    true
                }
            }
        }
    }

    private fun setMarkerIcon(sensor:CityResponse): BitmapDescriptor? {
        val image = when (sensor.quality.index) {
            in 0..50 -> R.drawable.icon_maps_green
            in 51..100 -> R.drawable.icon_maps_yellow
            in 101..150 -> R.drawable.icon_maps_orange
            in 151..200 -> R.drawable.icon_maps_red
            in 201..300 -> R.drawable.icon_maps_purple
            else -> R.drawable.icon_maps_danger
        }
        return if(context != null) {
            this.getBitmapMarker(requireContext(), image,"${sensor.quality.index}")?.let { BitmapDescriptorFactory.fromBitmap(it) }
        } else BitmapDescriptorFactory.fromResource(image)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.let {
            GoogleMap = it
        }
        val py = LatLng(-25.250, -57.536)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(py, 10f))
        googleMap.uiSettings.isMapToolbarEnabled = true
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
                    if (btnFilter.visibility == View.GONE) btnFilter.visibility = View.VISIBLE
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
                _binding.tvTitleCity.text = context?.getText(R.string.tvSensorCloser) ?: "Sensor más cercano:"
                if (::adapter.isInitialized){
                    filterAdapter = context?.getString(R.string.itemDistance) ?: "Distance"
                    tvFilter.text = context?.getText(R.string.tvDistance) ?: "Distancia"
                    isDown = true
                    orderList()
                }
            }
        }
    }

    private fun orderList(){
        adapter.orderList(filterAdapter,isDown)
        recycler.scrollBy(0,0)
    }

    private fun mapTheme(){
        if (::GoogleMap.isInitialized && context != null){
            btnArrow = _binding.coordinatorLayout.findViewById(R.id.btnArrow)
            if (!viewModel.isNotDefaultTheme()){
                ThemeState.isDark = this.getModeTheme(requireContext())
                ThemeState.isDefault = true
            }else{
                ThemeState.isDark = viewModel.getTheme()
                ThemeState.isDefault = false
            }
            if (ThemeState.isDark){
                GoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle_night))
                btnArrow.setTint(R.color.white)
                _binding.tvTitle.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }else{
                GoogleMap.mapType = com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL
                btnArrow.setTint(R.color.black)
                _binding.tvTitle.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
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