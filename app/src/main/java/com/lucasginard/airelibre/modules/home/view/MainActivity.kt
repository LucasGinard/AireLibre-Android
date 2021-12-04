package com.lucasginard.airelibre.modules.home.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var bindding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindding.root)
        configureFragment()
    }

    private fun configureFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(bindding.fragmentHome.id, HomeFragment.newInstance())
        ft.commit()
    }

    override fun onBackPressed() {
        val map = bindding.fragmentHome.findViewById<CoordinatorLayout>(R.id.coordinator_layout)
        val listState = BottomSheetBehavior.from(map.findViewById(R.id.bottomSheet))
        if (listState.state == BottomSheetBehavior.STATE_EXPANDED){
            listState.state = BottomSheetBehavior.STATE_COLLAPSED
        }else{
            moveTaskToBack(true)
        }
    }
}