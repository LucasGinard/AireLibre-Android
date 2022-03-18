package com.lucasginard.airelibre.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.net.Uri
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
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

fun String.textAQI(aqiEscale:Int,context: Context):String {
    when (aqiEscale) {
        in 0..50 -> {
             return context?.getString(R.string.tvDescriptionGreen)
        }
        in 51..100 -> {
            return context?.getString(R.string.tvDescriptionYellow)
        }
        in 101..150 -> {
            return context?.getString(R.string.tvDescriptionOrange)
        }
        in 151..200 -> {
            return context?.getString(R.string.tvDescriptionRed)
        }
        in 201..300 -> {
            return context?.getString(R.string.tvDescriptionPurple)
        }
        else -> {
            return context?.getString(R.string.tvDescriptionDanger)
        }
    }
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

fun Fragment.requireContentView(
    compositionStrategy: ViewCompositionStrategy = ViewCompositionStrategy.DisposeOnDetachedFromWindow,
    context: Context = requireContext(),
    content: @Composable () -> Unit
): ComposeView {
    val view = ComposeView(context)
    view.setViewCompositionStrategy(compositionStrategy)
    view.setContent(content)
    return view
}

fun Fragment.contentView(
    compositionStrategy: ViewCompositionStrategy = ViewCompositionStrategy.DisposeOnDetachedFromWindow,
    context: Context? = getContext(),
    content: @Composable () -> Unit
): ComposeView? {
    context ?: return null
    val view = ComposeView(context)
    view.setViewCompositionStrategy(compositionStrategy)
    view.setContent(content)
    return view
}

fun Fragment.getBitmapMarker(context: Context, resourceId: Int, mText: String): Bitmap? {
    val background = ContextCompat.getDrawable(context, resourceId)
    background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(
        background.intrinsicWidth,
        background.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    background.draw(canvas)
    val r = Rect()
    canvas.getClipBounds(r)
    val cHeight = r.height()
    val cWidth = r.width()
    val paint = Paint()
    paint.textAlign = Paint.Align.LEFT
    paint.textSize = 24f
    paint.isFakeBoldText = true
    paint.getTextBounds(mText, 0, mText.length, r)
    val x = cWidth / 2f - r.width() / 3f - r.left
    val y = cHeight / 2.5f + r.height() / 2f - r.bottom
    canvas.drawText(mText, x, y, paint)
    return bitmap
}