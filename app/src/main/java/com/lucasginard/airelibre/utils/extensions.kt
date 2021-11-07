package com.lucasginard.airelibre.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.lucasginard.airelibre.R

fun View.setTint(color: Int = R.color.primaryColor) {
    this.backgroundTintList = ContextCompat.getColorStateList(this.context, color)
}

fun View.animationCreate(drawable: Int): Animation? {
    return AnimationUtils.loadAnimation(context, drawable)
}

fun Intent.goToURL(url: String, context: Context) {
    val uri: Uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}

fun Fragment.textsAQI(viewDescription: TextView?=null, viewIcon: TextView, viewQuality: TextView, index: Int) {
    when (index) {
        in 0..50 -> {
            viewIcon.text = context?.getString(R.string.emojiGreen)
            viewDescription?.text = context?.getString(R.string.tvDescriptionGreen)
        }
        in 51..100 -> {
            viewIcon.text = context?.getString(R.string.emojiYellow)
            viewDescription?.text = context?.getString(R.string.tvDescriptionYellow)
        }
        in 101..150 -> {
            viewIcon.text = context?.getString(R.string.emojiOrange)
            viewDescription?.text = context?.getString(R.string.tvDescriptionOrange)
        }
        in 151..200 -> {
            viewIcon.text = context?.getString(R.string.emojiRed)
            viewDescription?.text = context?.getString(R.string.tvDescriptionRed)
        }
        in 201..300 -> {
            viewIcon.text = context?.getString(R.string.emojiPurple)
            viewDescription?.text = context?.getString(R.string.tvDescriptionPurple)
        }
        else -> {
            viewIcon.text = context?.getString(R.string.emojiDanger)
            viewDescription?.text = context?.getString(R.string.tvDescriptionDanger)
        }
    }
    viewQuality.text = index.toString()
}

fun View.resizeSmall(){
    this.updateLayoutParams{
        this.height = 240
    }
    this.requestLayout()
}

fun View.resizeLarge(){
    this.updateLayoutParams{
        this.height = ViewGroup.LayoutParams.FILL_PARENT
    }
    this.requestLayout()
}