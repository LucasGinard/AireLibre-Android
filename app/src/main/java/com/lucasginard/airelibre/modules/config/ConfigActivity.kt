package com.lucasginard.airelibre.modules.config

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.config.domain.ConfigRepository
import com.lucasginard.airelibre.modules.config.ui.theme.AireLibreTheme
import com.lucasginard.airelibre.modules.config.viewModel.ConfigViewModel
import com.lucasginard.airelibre.modules.config.viewModel.ConfigViewModelFactory
import com.lucasginard.airelibre.modules.home.view.MainActivity
import com.lucasginard.airelibre.utils.ComposablesUtils
import com.lucasginard.airelibre.utils.ThemeState


class ConfigActivity : ComponentActivity() {

    private lateinit var viewModel: ConfigViewModel
    private lateinit var locationPermissionRequest:
            ActivityResultLauncher<Array<String>>

    lateinit var checkedStateTheme: MutableState<Boolean>
    lateinit var showDialogLocation:MutableState<Boolean>
    lateinit var checkedStateLocation :MutableState<Boolean>

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
        viewModel = ViewModelProvider(this, ConfigViewModelFactory(ConfigRepository())).get(
            ConfigViewModel::class.java
        )
        setContent {
            AireLibreTheme(darkTheme = ThemeState.isDark) {
                Surface(color = MaterialTheme.colors.background) {
                    baseConfig(this)
                }
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
                    val uri: Uri = Uri.fromParts("package", packageName, null)
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
        if (!viewModel.getFlatTheme()) {
            //
        }
        if (::checkedStateLocation.isInitialized) checkedStateLocation.value = checkPermissionLocation()
    }

    override fun onBackPressed() {
        startActivity(
            Intent(this, MainActivity::class.java),
            ActivityOptions.makeCustomAnimation(this, R.anim.slide_left, R.anim.slide_right)
                .toBundle()
        )
    }

    @Composable
    fun baseConfig(activity: ConfigActivity) {
        Column(
            modifier = Modifier
                .padding(end = 20.dp, start = 20.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            sectionTitle()
            sectionSwitchLocation()
            sectionSwitchTheme()
            dialogDenyComposable()

            IconButton(
                modifier = Modifier
                    .then(Modifier.size(60.dp))
                    .padding(top = 15.dp),
                onClick = {
                    activity.startActivity(
                        Intent(activity, MainActivity::class.java),
                        ActivityOptions.makeCustomAnimation(
                            activity,
                            R.anim.slide_left,
                            R.anim.slide_right
                        ).toBundle()
                    )
                },
            ) {
                Icon(
                    modifier = Modifier.then(Modifier.size(60.dp)),
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = stringResource(id = R.string.contentOnBack),
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }

    @Composable
    fun sectionTitle() {
        Icon(
            modifier = Modifier
                .width(80.dp)
                .height(80.dp),
            painter = painterResource(id = R.drawable.icon_config),
            contentDescription = stringResource(id = R.string.contentLogo),
            tint = MaterialTheme.colors.primary
        )
        Text(
            text = stringResource(id = R.string.titleConfig),
            fontFamily = ComposablesUtils.fonts,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
        )
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
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        var permissionl = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permission == 0 || permissionl == 0
    }

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
                        viewModel.setFlatTheme(true)
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
            checkSwitchTheme(checkedStateTheme)
            Switch(
                checked = checkedStateTheme.value,
                onCheckedChange = {
                    if (!viewModel.getFlatTheme()) {
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
        }
    }

    @Composable
    private fun checkSwitchTheme(checkedState: MutableState<Boolean>) {
        if (!viewModel.getFlatTheme()) {
            checkedState.value = isSystemInDarkTheme()
        } else {
            checkedState.value = viewModel.getTheme()
        }
    }

    private fun switchTheme(switch: Boolean) {
        viewModel.setFlatTheme(true)
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

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview2() {
        AireLibreTheme {
            baseConfig(this)
        }
    }
}