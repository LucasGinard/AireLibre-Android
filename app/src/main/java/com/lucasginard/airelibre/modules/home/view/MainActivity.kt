package com.lucasginard.airelibre.modules.home.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureNav()
    }

    private fun configureNav() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        binding.navView.setupWithNavController(navHostFragment.navController)
    }

    override fun onBackPressed() {
        val map = binding.fragment.findViewById<CoordinatorLayout>(R.id.coordinator_layout)
        if (map != null){
            val listState = BottomSheetBehavior.from(map.findViewById(R.id.bottomSheet))
            if (listState.state == BottomSheetBehavior.STATE_EXPANDED){
                listState.state = BottomSheetBehavior.STATE_COLLAPSED
                return
            }
        } else if(binding.navView.selectedItemId == R.id.aboutFragment || binding.navView.selectedItemId == R.id.configFragment){
            binding.navView.selectedItemId = R.id.homeFragment
            return
        }
        moveTaskToBack(true)
    }
}