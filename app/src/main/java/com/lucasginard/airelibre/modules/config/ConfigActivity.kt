package com.lucasginard.airelibre.modules.config

import android.Manifest
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.lucasginard.airelibre.BuildConfig
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.config.domain.ConfigRepository
import com.lucasginard.airelibre.modules.config.ui.theme.AireLibreTheme
import com.lucasginard.airelibre.modules.config.viewModel.ConfigViewModel
import com.lucasginard.airelibre.modules.config.viewModel.ConfigViewModelFactory
import com.lucasginard.airelibre.modules.home.view.MainActivity
import com.lucasginard.airelibre.utils.ComposablesUtils
import com.lucasginard.airelibre.utils.ThemeState
import com.lucasginard.airelibre.utils.getModeTheme


class ConfigActivity : ComponentActivity() {



    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


}