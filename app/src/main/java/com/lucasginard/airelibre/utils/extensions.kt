package com.lucasginard.airelibre.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.lucasginard.airelibre.R

fun View.setTint(color:Int = R.color.primaryColor){
    this.backgroundTintList = ContextCompat.getColorStateList(this.context , color)
}

fun View.animationCreate(drawable: Int): Animation? {
    return AnimationUtils.loadAnimation(context, drawable)
}

