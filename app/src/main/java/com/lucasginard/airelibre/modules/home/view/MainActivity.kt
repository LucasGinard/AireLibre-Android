package com.lucasginard.airelibre.modules.home.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.databinding.ActivityMainBinding
import com.lucasginard.airelibre.modules.about.AboutFragment
import com.lucasginard.airelibre.modules.config.ConfigFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bindding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        bindding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindding.root)
        configureFragment()
        configureNav()
    }

    private fun configureNav() {
        bindding.navView.selectedItemId = R.id.nav_home
        bindding.navView.setOnItemSelectedListener{
            val fm: FragmentManager = supportFragmentManager
            val ft: FragmentTransaction = fm.beginTransaction()
            if (bindding.navView.selectedItemId != it.itemId){
                when(it.itemId){
                    R.id.nav_home ->{
                        ft.replace(bindding.fragmentHome.id, HomeFragment.newInstance())
                        ft.commit()
                        onResume()
                        true
                    }
                    R.id.nav_config ->{
                        ft.replace(bindding.fragmentHome.id, ConfigFragment.newInstance())
                        ft.commit()
                        onResume()
                        true
                    }
                    R.id.nav_about ->{
                        ft.replace(bindding.fragmentHome.id, AboutFragment.newInstance())
                        ft.commit()
                        onResume()
                        true
                    }
                    else -> {false}
                }
            }else{
                false
            }
        }
    }

    private fun configureFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(bindding.fragmentHome.id, HomeFragment.newInstance())
        ft.commit()
    }

    override fun onBackPressed() {
        val map = bindding.fragmentHome.findViewById<CoordinatorLayout>(R.id.coordinator_layout)
        if (map != null){
            val listState = BottomSheetBehavior.from(map.findViewById(R.id.bottomSheet))
            if (listState.state == BottomSheetBehavior.STATE_EXPANDED){
                listState.state = BottomSheetBehavior.STATE_COLLAPSED
                return
            }
        } else if(bindding.navView.selectedItemId == R.id.nav_about || bindding.navView.selectedItemId == R.id.nav_config){
            val fm: FragmentManager = supportFragmentManager
            val ft: FragmentTransaction = fm.beginTransaction()
            ft.replace(bindding.fragmentHome.id, HomeFragment.newInstance())
            ft.commit()
            bindding.navView.selectedItemId = R.id.nav_home
            return
        }
        moveTaskToBack(true)
    }
}