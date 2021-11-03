package com.lucasginard.airelibre.utils

import android.content.Context
import android.opengl.Visibility
import android.view.MotionEvent
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View
import android.view.ViewGroup
import com.lucasginard.airelibre.R
import java.lang.Exception
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs


class OnSwipeTouchListener internal constructor(ctx: Context, val mainView: View,val viewList:View ?=null) :
    View.OnTouchListener {
    private val gestureDetector: GestureDetector
    var context: Context

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    inner class GestureListener : SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > Companion.SWIPE_THRESHOLD && abs(velocityX) > Companion.SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                        result = true
                    }
                } else if (abs(diffY) > Companion.SWIPE_THRESHOLD && abs(velocityY) > Companion.SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0 && diffY != null) {
                        onSwipeBottom()
                    } else {
                        onSwipeTop()
                    }
                    result = true
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }

    }

    fun onSwipeRight() {
        onSwipe?.swipeRight()
    }

    fun onSwipeLeft() {
        onSwipe?.swipeLeft()
    }

    fun onSwipeTop() {
        if (mainView.height <= 340){
            mainView.startAnimation(mainView.animationCreate(R.anim.slide_up))
            if (mainView.id == 2131296757){
                val params: ViewGroup.LayoutParams = mainView.layoutParams
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                mainView.layoutParams = params
            }
            onSwipe?.swipeTop()
        }
    }

    fun onSwipeBottom() {
        mainView.startAnimation(mainView.animationCreate(R.anim.slide_down))
        if (mainView.id == 2131296757){
            val params: ViewGroup.LayoutParams = mainView.layoutParams
            params.height = 340
            mainView.layoutParams = params
        }else{
            mainView.visibility = View.GONE
            viewList?.visibility = View.VISIBLE
        }
        onSwipe?.swipeBottom()
    }

    interface onSwipeListener {
        fun swipeRight()
        fun swipeTop()
        fun swipeBottom()
        fun swipeLeft()
    }

    var onSwipe: onSwipeListener? = null

    init {
        gestureDetector = GestureDetector(ctx, GestureListener())
        mainView.setOnTouchListener(this)
        context = ctx
    }

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }
}