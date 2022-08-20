package com.lucasginard.airelibre.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.NetworkOnMainThreadException
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.lucasginard.airelibre.R
import java.io.IOException


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
             return context.getString(R.string.tvDescriptionGreen)
        }
        in 51..100 -> {
            return context.getString(R.string.tvDescriptionYellow)
        }
        in 101..150 -> {
            return context.getString(R.string.tvDescriptionOrange)
        }
        in 151..200 -> {
            return context.getString(R.string.tvDescriptionRed)
        }
        in 201..300 -> {
            return context.getString(R.string.tvDescriptionPurple)
        }
        else -> {
            return context.getString(R.string.tvDescriptionDanger)
        }
    }
}

fun TextView.descriptionAQI(index:Int):String{
    when (index) {
        in 0..50 -> {
            return context.getString(R.string.titleGreen)
        }
        in 51..100 -> {
            return context.getString(R.string.titleYellow)
        }
        in 101..150 -> {
            return context.getString(R.string.titleOrange)
        }
        in 151..200 -> {
            return context.getString(R.string.titleRed)
        }
        in 201..300 -> {
            return context.getString(R.string.titlePurple)
        }
        else -> {
            return context.getString(R.string.titleDanger)
        }
    }
}

fun TextView.colorBackground(quality:Int,context:Context):Int {
    when (quality) {
        in 0..50 -> {
            return context.getColor(R.color.cardGreen)
        }
        in 51..100 -> {
            return context.getColor(R.color.cardYellow)
        }
        in 101..150 -> {
            return context.getColor(R.color.cardOrange)
        }
        in 151..200 -> {
            return context.getColor(R.color.cardRed)
        }
        in 201..300 -> {
            return context.getColor(R.color.cardPurple)
        }
        else -> {
            return context.getColor(R.color.cardDanger)
        }
    }
}

fun View.animationArrow(rotation:Float) {
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

fun Modifier.vertical() = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.height, placeable.width) {
        placeable.place(
            x = -(placeable.width / 2 - placeable.height / 2),
            y = -(placeable.height / 2 - placeable.width / 2)
        )
    }
}

fun Fragment.isInternetAvailable(): Boolean {
    try {
        val ipProcess = Runtime.getRuntime().exec("/system/bin/ping -c 1 8.8.8.8")
        return ipProcess.waitFor() == 0
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
    return false
}