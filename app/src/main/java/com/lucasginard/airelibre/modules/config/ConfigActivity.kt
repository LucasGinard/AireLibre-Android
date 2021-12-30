package com.lucasginard.airelibre.modules.config

import android.Manifest
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.about.descriptionContributeProject
import com.lucasginard.airelibre.modules.about.sectionLicenseLogo
import com.lucasginard.airelibre.modules.about.sectionSocialMedia
import com.lucasginard.airelibre.modules.about.sectionWhatisAire
import com.lucasginard.airelibre.modules.config.ui.theme.AireLibreTheme
import com.lucasginard.airelibre.modules.home.view.MainActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ConfigActivity : ComponentActivity() {
    private lateinit var locationPermissionRequest:
            ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        0
                    )
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        0
                    )
                }
                else -> {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            }
        }
        setContent {
            AireLibreTheme {
                Surface(color = MaterialTheme.colors.background) {
                    baseConfig(this)
                }
            }
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

    override fun onBackPressed() {
        startActivity(
            Intent(this, MainActivity::class.java),
            ActivityOptions.makeCustomAnimation(this, R.anim.slide_left, R.anim.slide_right)
                .toBundle()
        )
    }

    @Composable
    fun baseConfig(activity: ConfigActivity) {
        val fonts = FontFamily(
            Font(R.font.rubik_bold, weight = FontWeight.Bold),
            Font(R.font.rubik_regular, weight = FontWeight.Normal)
        )
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .padding(end = 20.dp, start = 20.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            sectionTitle(fonts)
            sectionSwitchLocation(fonts)

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
                        )
                            .toBundle()
                    )
                },
            ) {
                Icon(
                    modifier = Modifier.then(Modifier.size(60.dp)),
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = "onBack",
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }

    @Composable
    fun sectionTitle(fonts: FontFamily) {
        Icon(
            modifier = Modifier
                .width(80.dp)
                .height(80.dp),
            painter = painterResource(id = R.drawable.icon_config),
            contentDescription = "logoIcon",
            tint = MaterialTheme.colors.primary
        )
        Text(
            text = "Ajustes",
            fontFamily = fonts,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
    }

    @Composable
    private fun sectionSwitchLocation(fonts: FontFamily) {
        var checkedState = remember { mutableStateOf(false) }
        //checkedState.value = checkPermissionLocation()
        Row(
            Modifier.padding(top = 10.dp, bottom = 10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_maps_red),
                contentDescription = "iconLocation",
                modifier = Modifier
                    .height(20.dp)
                    .width(20.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
            Text(
                text = "Ubicación:",
                fontFamily = fonts,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(end = 5.dp)
            )
            Switch(
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it
                    if (it) {
                        requestLocation()
                    }
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

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview2() {
        AireLibreTheme {
            baseConfig(this)
        }
    }
}

