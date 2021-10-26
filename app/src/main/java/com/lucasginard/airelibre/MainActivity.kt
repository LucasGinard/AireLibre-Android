package com.lucasginard.airelibre

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lucasginard.airelibre.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var bindding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindding.root)

    }
}