package com.lucasginard.airelibre.modules.home.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        finish()
    }
}