package com.lucasginard.airelibre.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
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

fun Fragment.getModeTheme(context: Context):Boolean {
    val nightModeFlags = context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK
    return when (nightModeFlags) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        else -> false
    }
}
fun Activity.getModeTheme(context: Context):Boolean {
    val nightModeFlags = context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK
    return when (nightModeFlags) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        else -> false
    }
}

fun Fragment.textsAQI(
    viewDescription: TextView? = null,
    viewIcon: TextView,
    viewQuality: TextView,
    index: Int
) {
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

fun View.animationList(rotation:Float) {
    this.animate()
        .rotation(
            if (this.rotation == 90F)
                -90F
            else 90F
        )
        .withEndAction { this.rotation = rotation }
        .start()
}
fun View.animationRefresh() {
    this.animate()
        .rotation(
            360f
        )
        .withEndAction { this.rotation }
        .start()
}

fun Fragment.ToastCustom(text: String) {
    Toast.makeText(
        this.context,
        text,
        Toast.LENGTH_SHORT
    ).show()
}
val String.color
    get() = Color(android.graphics.Color.parseColor(this))