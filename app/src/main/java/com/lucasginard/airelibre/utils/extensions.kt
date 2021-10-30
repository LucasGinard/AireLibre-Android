package com.lucasginard.airelibre.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
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

fun Intent.goToURL(url:String, context: Context){
    val uri: Uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}
