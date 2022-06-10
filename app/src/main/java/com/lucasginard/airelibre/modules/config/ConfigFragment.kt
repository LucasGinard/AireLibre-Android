package com.lucasginard.airelibre.modules.config

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.lucasginard.airelibre.BuildConfig
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.config.dialogColorPicker.DialogColorPicker
import com.lucasginard.airelibre.modules.config.listMaps.SelectMapCards
import com.lucasginard.airelibre.modules.config.ui.theme.AireLibreTheme
import com.lucasginard.airelibre.modules.config.viewModel.ConfigViewModel
import com.lucasginard.airelibre.modules.home.view.MainActivity
import com.lucasginard.airelibre.utils.*

class ConfigFragment: Fragment() {

    private val viewModel: ConfigViewModel by viewModels()
    private lateinit var locationPermissionRequest:
            ActivityResultLauncher<Array<String>>

    lateinit var checkedStateTheme: MutableState<Boolean>
    lateinit var showDialogLocation: MutableState<Boolean>
    lateinit var checkedStateLocation : MutableState<Boolean>
    lateinit var visibilityRestoreTheme : MutableState<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    if (viewModel.getFlatLocationSucess()){
                        showDialogLocation.value = true
                    }else{
                        viewModel.setFlatLocationSucess(true)
                    }
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {

                }
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                        if (viewModel.getFlatLocationDeny()){
                            showDialogLocation.value = true
                        }else{
                            viewModel.setFlatLocationDeny(true)
                        }
                    } else{
                        //para versiones inferiores a android 11.0.
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )= requireContentView(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)) {
        AireLibreTheme(darkTheme = ThemeState.isDark) {
            Surface(color = MaterialTheme.colors.background) {
                baseConfig()
            }
        }
    }

    @Composable
    fun dialogDenyComposable() {
        var textDialog = if(::checkedStateLocation.isInitialized && checkedStateLocation.value) R.string.descriptionAcceptLocation else R.string.descriptionDenyLocation
        showDialogLocation = remember { mutableStateOf(false) }
        if (showDialogLocation.value) {
            ComposablesUtils.dialogCustom(
                openDialog = showDialogLocation,
                text = textDialog,
                btnAccept = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    intent.data = uri
                    startActivity(intent)
                })
        }
    }

    fun requestLocation() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onResume() {
        super.onResume()
        if ( !viewModel.isNotDefaultTheme()) {
            ThemeState.isDark = this.getModeTheme(requireContext())
        }
        if (::checkedStateLocation.isInitialized) checkedStateLocation.value = checkPermissionLocation()
    }

    @ExperimentalAnimationApi
    @Composable
    fun baseConfig() {
        Column(
            modifier = Modifier
                .padding(end = 20.dp, start = 20.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            sectionSwitchLocation()
            sectionSwitchTheme()
            sectionColorPicker()
            SelectMapCards(viewModel,requireContext())
            sectionTextVersion()
            dialogDenyComposable()

        }
    }

    @Composable
    private fun sectionSwitchLocation() {
        checkedStateLocation = remember { mutableStateOf(false) }
        checkedStateLocation.value = checkPermissionLocation()
        Row(
            Modifier.padding(top = 20.dp, bottom = 10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_maps_red),
                contentDescription = stringResource(id = R.string.contentLocation),
                modifier = Modifier
                    .height(20.dp)
                    .width(20.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
            Text(
                text = stringResource(id = R.string.titleSwitchLocation),
                fontFamily = ComposablesUtils.fonts,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(end = 5.dp)
            )
            Switch(
                checked = checkedStateLocation.value,
                onCheckedChange = {
                    checkedStateLocation.value = it
                    requestLocation()
                },
                enabled = true,
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
            )
        }

    }

    private fun checkPermissionLocation(): Boolean {
        var permission = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        var permissionl = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permission == 0 || permissionl == 0
    }

    @ExperimentalAnimationApi
    @Composable
    private fun sectionSwitchTheme() {
        checkedStateTheme = remember { mutableStateOf(false) }
        Row(
            Modifier.padding(top = 10.dp, bottom = 10.dp)
        ) {
            var openDialog = remember { mutableStateOf(false) }
            if (openDialog.value) {
                ComposablesUtils.dialogCustom(
                    openDialog = openDialog,
                    text = R.string.descriptionWarningTheme,
                    btnAccept = {
                        viewModel.setNotIsDefaultTheme(true)
                        checkedStateTheme.value = !checkedStateTheme.value
                        switchTheme(checkedStateTheme.value)
                    })
            }
            Image(
                painter = painterResource(id = R.drawable.ic_theme_mode),
                contentDescription = stringResource(id = R.string.contentLogo),
                modifier = Modifier
                    .height(20.dp)
                    .width(20.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
            Text(
                text = stringResource(id = R.string.titleSwitchTheme),
                fontFamily = ComposablesUtils.fonts,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(end = 5.dp)
            )
            checkSwitchTheme(checkedStateTheme, isSystemInDarkTheme())
            val themeSystem = isSystemInDarkTheme()
            Switch(
                checked = checkedStateTheme.value,
                onCheckedChange = {
                    if (!viewModel.isNotDefaultTheme()) {
                        openDialog.value = true
                    } else {
                        checkedStateTheme.value = it
                        switchTheme(it)
                    }
                },
                enabled = true,
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
            )
            visibilityRestoreTheme = remember { mutableStateOf(false) }
            visibilityRestoreTheme.value = viewModel.isNotDefaultTheme()
            AnimatedVisibility(
                visible = visibilityRestoreTheme.value
            ) {
                IconButton(
                    onClick = {
                        viewModel.setNotIsDefaultTheme(false)
                        checkSwitchTheme(checkedStateTheme,themeSystem)
                        visibilityRestoreTheme.value = !visibilityRestoreTheme.value
                    }
                ) {
                    Icon(
                        modifier = Modifier.then(Modifier.size(35.dp)),
                        painter = painterResource(id = R.drawable.ic_rollback),
                        contentDescription = stringResource(id = R.string.contentOnBack),
                        tint = MaterialTheme.colors.primary
                    )
                }
            }

        }
    }

    @Composable
    fun sectionColorPicker(){
        var openDialogColor = remember { mutableStateOf(false) }
        var colorCustom = remember { mutableStateOf(Color(0xFFFFFFFF)) }

        if (openDialogColor.value) {
            DialogColorPicker(openDialog = openDialogColor,colorCustom,
                requireActivity() as MainActivity
            )
        }
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                fontFamily = ComposablesUtils.fonts,
                fontWeight = FontWeight.Bold,
                text = "Personaliza el color"
            )
            var selectedOption by remember {
                mutableStateOf("Pred")
            }
            val onSelectionChange = { text: String ->
                selectedOption = text
            }
            Row(modifier = Modifier
                .padding(top = 10.dp)
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth()) {
                Column(
                    modifier= Modifier.padding(start = 10.dp, end = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.map_default),
                        contentDescription = "selectedColor",
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(Color(0xFF047745)),
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(1.dp, if (selectedOption == "Pred") Color.Green else Color.Gray, CircleShape)
                            .clickable(
                                onClick = {
                                    onSelectionChange("Pred")
                                }
                            ),
                    )
                    Text(
                        modifier = Modifier.paddingFromBaseline(top = 20.dp),
                        text = "Pred",
                        fontFamily = ComposablesUtils.fonts,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp
                    )
                }
                Column(
                    modifier= Modifier.padding(start = 10.dp, end = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.map_default),
                        contentDescription = "selectedColor",
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(colorCustom.value),
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(1.dp, if (selectedOption == "Personalizado") Color.Green else Color.Gray, CircleShape)
                            .clickable(
                                onClick = {
                                    openDialogColor.value = true
                                    onSelectionChange("Personalizado")
                                }
                            ),
                    )
                    Text(
                        modifier = Modifier.paddingFromBaseline(top = 20.dp),
                        text = "Personalizado",
                        fontFamily = ComposablesUtils.fonts,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }


    @Composable
    private fun sectionTextVersion(){
        Text(
            text = "${getString(R.string.titleVersion)} ${BuildConfig.VERSION_NAME}",
            modifier = Modifier.padding(top = 10.dp ,bottom =  10.dp),
            fontFamily = ComposablesUtils.fonts,
            fontWeight = FontWeight.Normal,
        )
    }

    private fun checkSwitchTheme(checkedState: MutableState<Boolean>,themeSystem:Boolean) {
        if (!viewModel.isNotDefaultTheme()) {
            checkedState.value = themeSystem
            ThemeState.isDark = checkedState.value
        } else {
            checkedState.value = viewModel.getTheme()
            ThemeState.isDark = checkedState.value
        }
    }

    private fun switchTheme(switch: Boolean) {
        viewModel.setNotIsDefaultTheme(true)
        when (switch) {
            true -> {
                ThemeState.isDark = true
                viewModel.setTheme(true)
            }
            false -> {
                ThemeState.isDark = false
                viewModel.setTheme(false)
            }
        }
    }

    @ExperimentalAnimationApi
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview2() {
        AireLibreTheme {
            baseConfig()
        }
    }

    companion object {
        fun newInstance() = ConfigFragment()
    }
}