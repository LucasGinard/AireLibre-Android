package com.lucasginard.airelibre.modules.home.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var bindding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        bindding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindding.root)
        configureNav()
    }

    private fun configureNav() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        bindding.navView.setupWithNavController(navHostFragment.navController)
    }

    override fun onBackPressed() {
        val map = bindding.fragment.findViewById<CoordinatorLayout>(R.id.coordinator_layout)
        if (map != null){
            val listState = BottomSheetBehavior.from(map.findViewById(R.id.bottomSheet))
            if (listState.state == BottomSheetBehavior.STATE_EXPANDED){
                listState.state = BottomSheetBehavior.STATE_COLLAPSED
                return
            }
        } else if(bindding.navView.selectedItemId == R.id.aboutFragment || bindding.navView.selectedItemId == R.id.configFragment){
            val fm: FragmentManager = supportFragmentManager
            val ft: FragmentTransaction = fm.beginTransaction()
            ft.replace(bindding.fragment.id, HomeFragment.newInstance())
            ft.commit()
            bindding.navView.selectedItemId = R.id.homeFragment
            return
        }
        moveTaskToBack(true)
    }
}