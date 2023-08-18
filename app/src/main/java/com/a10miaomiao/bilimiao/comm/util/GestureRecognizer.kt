package com.duzhaokun123.bilibilihd2.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs

public class GestureRecognizer(val context: Context, val listener: OnGestureListener) {

    enum class SwipeDirection {
        UP, DOWN, LEFT, RIGHT
    }

    /**
     * 手势探测器
     */
    private lateinit var mGestureDetectorCompat: GestureDetectorCompat
//    private val mGestureDetectorCompat: GestureDetectorCompat by lazy {
//        GestureDetectorCompat(context, this.mGestureListener)
//    }

    /**
     * 长按标记
     */
    private var mInLongPress = false

    /**
     * @note This method will cover all the view's other touch events.
     */
//    @SuppressLint("ClickableViewAccessibility")
//    public fun attachToView(view: View) {
//        mGestureDetectorCompat = GestureDetectorCompat(context, mGestureListener)
//
//        view.setOnTouchListener { _, event ->
//            onTouchEvent(event)
//        }
//    }

    init {
        mGestureDetectorCompat = GestureDetectorCompat(context, gestureListener())
    }

    public fun onTouchEvent(event: MotionEvent): Boolean {

        val result = mGestureDetectorCompat.onTouchEvent(event)
        if (event.action == ACTION_UP || event.action == ACTION_CANCEL) {
            if (mInLongPress) onLongPressEnd(event)
        }
        return result
    }

    public interface OnGestureListener {

        fun onSingleTap(e: MotionEvent): Boolean { return false }

        fun onDoubleTap(e: MotionEvent): Boolean { return false }

        /**
         * MotionEvent.ACTION_DOWN
         * Also called when MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL
         */
        fun onLongPress(e: MotionEvent) { }

        fun onSwipe(direction: SwipeDirection) { }
    }

    private fun onLongPressEnd(event: MotionEvent) {
        mInLongPress = false
        listener.onLongPress(event)
    }

    /**
     * 手势监听
     */
    private fun gestureListener() = object : GestureDetector.SimpleOnGestureListener() {
        private val FLING_MIN_VELOCITY = 200 // 移动最小速度

        override fun onDown(event: MotionEvent): Boolean {
            return false
        }

        override fun onLongPress(event: MotionEvent) {
            mInLongPress = true
            listener.onLongPress(event)
        }

        override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
            return listener.onSingleTap(event)
        }

        override fun onDoubleTap(event: MotionEvent): Boolean {
            return listener.onDoubleTap(event)
        }

        override fun onFling(
            e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float
        ): Boolean {
            val translateX = e2.x - e1.x  // 水平移动距离
            val translateY = e2.y - e1.y  // 垂直移动距离
            // 垂直
            if (abs(velocityY) > FLING_MIN_VELOCITY && abs(translateY) > abs(translateX)) {
                // 向上滑动
                if (translateY < 0) {
                    listener.onSwipe(SwipeDirection.UP)
                    return true
                }
                // 向下滑动
                if (translateY > 0) {
                    listener.onSwipe(SwipeDirection.DOWN)
                    return true
                }
            }

            // 水平
            if (abs(velocityX) > FLING_MIN_VELOCITY && abs(translateX) > abs(translateY)) {
                // 向左滑动
                if (translateX < 0) {
                    listener.onSwipe(SwipeDirection.LEFT)
                    return true
                }
                // 向右滑动
                if (translateX > 0) {
                    listener.onSwipe(SwipeDirection.RIGHT)
                    return true
                }
            }
            return false
        }
    }
}