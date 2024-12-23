package com.lucasginard.airelibre.modules.home.view

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.databinding.FragmentHomeBinding
import com.lucasginard.airelibre.modules.config.ui.theme.AireLibreTheme
import com.lucasginard.airelibre.modules.home.model.CardsAQI
import com.lucasginard.airelibre.modules.home.model.SensorResponse
import com.lucasginard.airelibre.modules.home.view.dialog.DialogCardsAQICompose
import com.lucasginard.airelibre.modules.home.viewModel.HomeViewModel
import com.lucasginard.airelibre.utils.*
import com.lucasginard.airelibre.modules.home.view.adapter.AdapterSensorList
import com.lucasginard.airelibre.modules.home.view.dialog.DialogConfigureNotification
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback,ContractHome {

    private lateinit var _binding: FragmentHomeBinding
    private lateinit var adapter: AdapterSensorList
    private lateinit var sensorCloser: SensorResponse
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
    private lateinit var appUpdate:AppUpdateManager

    private lateinit var showAQIDialog:MutableState<Boolean>
    private lateinit var showNotificationDialog:MutableState<Boolean>
    private var listCards = ArrayList<CardsAQI>()
    private var markerList = ArrayList<Marker>()
    val viewModel: HomeViewModel by viewModels()

    private var mapView: MapView? = null
    private var listSensors = ArrayList<SensorResponse>()
    private var flatPermisson = false
    private var isDown = true
    private val REQUEST_UPDATE = 100
    private var zoomMap:Float ?=null

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
                        showAQIDialog = remember { androidx.compose.runtime.mutableStateOf(false) }
                        showNotificationDialog = remember { androidx.compose.runtime.mutableStateOf(false) }
                        if (showAQIDialog.value) DialogCardsAQICompose(showAQIDialog,listCards)
                        if (showNotificationDialog.value) DialogConfigureNotification(showNotificationDialog,viewModel.sensorNotify) {
                            updateAdapterItem(true)
                        }
                    }
                }
            }
        }
        requestLocation()
        return _binding.root
    }

    override fun showInfoMarker(marker: String) {
        _binding.linearInfoMarker.visibility = View.VISIBLE
        _binding.linearInfoMarker.startAnimation(
            _binding.linearInfoMarker.animationCreate(
                R.anim.slide_up
            )
        )
        val sensorObject = listSensors.find { it.description == marker }
        if (sensorObject != null){
            _binding.tvCiudad.text = sensorObject.description
            sensorObject.quality.index.let {
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
                        sensorObject.latitude,
                        sensorObject.longitude
                    ), zoomMap ?: 13f
                )
            )
        }
        markerList.find { it.title  == marker }?.showInfoWindow()

        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            btnArrow.background = ContextCompat.getDrawable(requireContext(),R.drawable.ic_arrow_up)
        }
        if (::sensorCloser.isInitialized) {
            if (sensorCloser.description == marker) {
                _binding.tvTitleSensor.text = context?.getText(R.string.tvSensorCloser) ?: "Sensor más cercano:"
            } else _binding.tvTitleSensor.text = context?.getText(R.string.tvSensor) ?: "Sensor:"
        } else {
            _binding.tvTitleSensor.text = context?.getText(R.string.tvSensor) ?: "Sensor:"
        }
    }


    override fun infoAQI() {
        listCards.clear()
        listCards = viewModel.getCards(requireActivity())
        showAQIDialog.value = true
    }

    override fun showDialogConfigure(sensor: SensorResponse) {
        viewModel.sensorNotify = sensor
        showNotificationDialog.value = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureAuth()
        configureUI()
        configureAdapter()
        configureServiceListSensors()
        configureOnClickListeners()
        configureMaps(savedInstanceState)
        checkUpdate()
        configureReviewPlayStore()
    }

    private fun configureAuth() {
        viewModel.authForRealtime()
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
            AdapterSensorList(listSensors, this, GoogleMap)
        }else{
            AdapterSensorList(listSensors, this)
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
        }
    }

    private fun configureOnClickListeners() {
        _binding.btnInfo.setOnClickListener {
            infoAQI()
        }

        _binding.btnClose.setOnClickListener {
            _binding.linearInfoMarker.startAnimation(it.animationCreate(R.anim.slide_down))
            _binding.linearInfoMarker.visibility = View.GONE
        }

        _binding.btnReconnect.setOnClickListener {
            configureServiceListSensors()
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
            viewModel.getAllSensors()
        }

        _binding.iconUpdate.setOnClickListener {
            _binding.tvGmt.setAnimationSlideLeftToRight(_binding.tvGmt.id)
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
                getText(R.string.tvNotifyTitle) -> {
                    filterAdapter = context?.getString(R.string.tvNotifyFilter) ?: "NotifyFilter"
                    tvFilter.text = getText(R.string.tvNotifyTitle)
                    orderList()
                    true
                }
                else -> false
            }
        }
        when (tvFilter.text) {
            getText(R.string.tvDistance) ->{
                popup.menu.findItem(R.id.distanceID).isVisible = false
            }
            getText(R.string.itemAQI) -> {
                popup.menu.findItem(R.id.aqiID).isVisible = false
            }
            getText(R.string.tvNotifyTitle) -> {
                popup.menu.findItem(R.id.NotiID).isVisible = false
            }
        }
        if (!flatPermisson) popup.menu.removeItem(R.id.distanceID)
        if (!viewModel.isActiveScheduleAlarmSensor()) popup.menu.removeItem(R.id.NotiID)
        popup.show()
    }

    private fun configureServiceListSensors() {
        viewModel.getListSensors.observe(requireActivity()) {
            if (it != null){
                markerList.clear()
                listSensors.clear()
                listSensors.addAll(it)
                adapter.notifyDataSetChanged()
                configureMarkers(listSensors)
                calculateMarkerLocation()
                if (_binding.btnReconnect.visibility == View.VISIBLE) {
                    _binding.btnReconnect.visibility = View.GONE
                    _binding.coordinatorLayout.visibility = View.VISIBLE
                }
                lastUpdateText(true)
            }else {
                errorConnectService()
            }

        }
        viewModel.errorMessage.observe(requireActivity()) {
            errorConnectService()
        }
        viewModel.getAllSensors()
    }

    private fun errorConnectService(){
        if (listSensors.isNotEmpty()){
            this.ToastCustom(context?.getString(R.string.toastErrorRetry) ?: "Hubo un problema intente de nuevo")
        }else{
            _binding.btnReconnect.visibility = View.VISIBLE
            _binding.coordinatorLayout.visibility = View.GONE
            if (activity != null) {
                this.ToastCustom(context?.getString(R.string.toastErrorNet) ?: "Sin conexión compruebe su conexión")
            }
        }
        lastUpdateText()
        callStatusService()
    }

    private fun callStatusService(){
        viewModel.getStatusService()
        viewModel.getStatus.observe(requireActivity()) {
            if (!it.getDataBaseIsUp() || !it.getServiceIsUp()){
                //TODO: implement toast error from service o database
            }
        }
    }

    private fun configureMaps(saved: Bundle?) {
        try {
            mapView = _binding.mapViewFragment
            mapView?.onCreate(saved)
            mapView?.onResume()

            MapsInitializer.initialize(requireActivity().applicationContext)

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
        } catch (e: Exception) {
            e.printStackTrace()
            errorConnectService()
        }
    }

    private fun configureMarkers(arrayList: ArrayList<SensorResponse>) {
        for (x in arrayList) {
            if (::GoogleMap.isInitialized && mapView != null) {
                GoogleMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(x.latitude, x.longitude))
                        .title(x.description)
                        .icon(setMarkerIcon(x))
                )?.let { markerList.add(it) }
            }
        }
        if (::GoogleMap.isInitialized){
            GoogleMap.setInfoWindowAdapter(object : InfoWindowAdapter {
                override fun getInfoWindow(arg0: Marker): View? {
                    return null
                }

                override fun getInfoContents(marker: Marker): View {
                    val sensor = listSensors.find { it.description == marker.title }
                    val info = LinearLayout(context)
                    info.orientation = LinearLayout.VERTICAL
                    val title = TextView(context)
                    val descriptionAQI = TextView(context)
                    if (sensor != null) {
                        descriptionAQI.text = descriptionAQI.descriptionAQI(sensor.quality.index)
                        descriptionAQI.setTextColor(descriptionAQI.colorBackground(sensor.quality.index,requireContext()))
                    }
                    descriptionAQI.textSize = 15f
                    descriptionAQI.setTypeface(null, Typeface.BOLD)
                    title.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
                    title.gravity = Gravity.CENTER
                    title.setTypeface(null, Typeface.BOLD)
                    title.text = marker.title
                    info.addView(title)
                    info.addView(descriptionAQI)
                    return info
                }
            })
            GoogleMap.setOnMarkerClickListener { marker ->
                showInfoMarker(marker.title!!)
                marker.showInfoWindow()
                true
            }
        }
    }

    private fun setMarkerIcon(sensor:SensorResponse): BitmapDescriptor? {
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
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(py, zoomMap ?:10f))
        googleMap.uiSettings.isMapToolbarEnabled = true
        //get Zoom Map
        googleMap.setOnCameraIdleListener {
            zoomMap = googleMap.cameraPosition.zoom
        }
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
                        }else{
                            flatPermisson = false
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

            for (x in listSensors) {
                distanceMarker.longitude = x.longitude
                distanceMarker.latitude = x.latitude
                val distancia = lastLocation.distanceTo(distanceMarker)
                x.distance = distancia/1000
                if (distanciaActual > distancia) {
                    posicionMasCercana = LatLng(x.latitude, x.longitude)
                    distanciaActual = distancia.toDouble();
                }
            }
            val cerca = listSensors.find { it.latitude == posicionMasCercana.latitude && it.longitude == posicionMasCercana.longitude }
            if (cerca != null) {
                sensorCloser = cerca
                showInfoMarker(cerca.description)
                markerList.find { it.title == cerca.description }?.showInfoWindow()
                _binding.tvTitleSensor.text = context?.getText(R.string.tvSensorCloser) ?: "Sensor más cercano:"
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
            val mapStyle = viewModel.getCustomMap()
            if (mapStyle == getString(R.string.mapDefault)){
                if (ThemeState.isDark){
                    GoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle_night))
                    changeColorTitleMap(false)
                }else{
                    GoogleMap.mapType = com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL
                    changeColorTitleMap(true)
                }
            }else{
                getCustomMap(mapStyle)
            }

        }
    }

    private fun getCustomMap(mapStyle:String){
        when(mapStyle){
            getString(R.string.mapGTA) -> {
                GoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle_gta))
                changeColorTitleMap(false)
            }
            getString(R.string.mapUber) ->{
                GoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle_uber))
                changeColorTitleMap(true)
            }
            getString(R.string.mapBlue) ->{
                GoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle_blue))
                changeColorTitleMap(false)
            }
            getString(R.string.mapRetro) ->{
                GoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle_retro))
                changeColorTitleMap(true)
            }
            getString(R.string.mapLightBlue) ->{
                GoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle_light_blue))
                changeColorTitleMap(true)
            }
            getString(R.string.mapFallout) ->{
                GoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle_fallout))
                changeColorTitleMap(false)
            }
            getString(R.string.mapCyber) ->{
                GoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle_cyber))
                changeColorTitleMap(false)
            }
            getString(R.string.mapSatellite) ->{
                GoogleMap.mapType = com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE
                changeColorTitleMap(false)
            }
        }
    }

    private fun changeColorTitleMap(isDark:Boolean){
        if (isDark){
            btnArrow.setTint(R.color.black)
            _binding.tvTitle.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
            _binding.tvGmt.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
            _binding.iconUpdate.setTint(R.color.black)
        }else{
            btnArrow.setTint(R.color.white)
            _binding.tvTitle.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            _binding.tvGmt.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            _binding.iconUpdate.setTint(R.color.white)
        }
    }

    private fun checkUpdate() {
        appUpdate = AppUpdateManagerFactory.create(requireContext())
        appUpdate.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && it.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                appUpdate.startUpdateFlowForResult(it,AppUpdateType.IMMEDIATE,requireActivity(),REQUEST_UPDATE)
            }
        }
    }

    private fun configureReviewPlayStore(){
        viewModel.showReviewForPlayStore(this)
    }

    private fun lastUpdateText(showGmt:Boolean = false){
        if (showGmt){
            _binding.linearGmt.visibility = View.VISIBLE
            _binding.tvGmt.text = SessionCache.lastUpdate
        }else{
            _binding.linearGmt.visibility = View.GONE
        }
    }

    fun updateAdapterItem(isEnable:Boolean){
        val positionForUpdate = listSensors.indexOf(viewModel.sensorNotify)
        listSensors[positionForUpdate].isEnableNotification = isEnable
        adapter.notifyItemChanged(positionForUpdate)
    }

    override fun onResume() {
        super.onResume()
        try{
            mapView?.onResume()
            mapTheme()
        }catch (e:Exception){}
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}

interface ContractHome{
    fun infoAQI()
    fun showInfoMarker(marker: String)
    fun showDialogConfigure(sensor: SensorResponse)
}