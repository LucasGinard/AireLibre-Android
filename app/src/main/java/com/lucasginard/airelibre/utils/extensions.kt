package com.lucasginard.airelibre.utils

import android.view.View
import androidx.core.content.ContextCompat
import com.lucasginard.airelibre.R

fun View.setTint(color:Int = R.color.primaryColor){
    this.backgroundTintList = ContextCompat.getColorStateList(this.context , color)
}

