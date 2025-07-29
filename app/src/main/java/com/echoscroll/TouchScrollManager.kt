package com.echoscroll

import android.content.Context
import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlin.math.abs

class TouchScrollManager(private val context: Context) {

    private var isTouchModeEnabled = false
    private var lastTouchPosition = PointF()
    private var touchSensitivity = 0.5f // 0.0 to 1.0
    private var isScrollingFromTouch = false
    private var touchScrollHandler = Handler(Looper.getMainLooper())
    private var touchScrollRunnable: Runnable? = null
    
    // Touch gesture detection
    private var lastTouchTime = 0L
    private var touchCount = 0
    private val doubleTapThreshold = 300L // ms
    private val longPressThreshold = 500L // ms
    
    // Smart scrolling parameters
    private var currentScrollSpeed = 3
    private var adaptiveScrolling = true
    private var autoResumeEnabled = true
    
    interface TouchScrollListener {
        fun onTouchScrollStart(x: Float, y: Float)
        fun onTouchScrollStop()
        fun onTouchGesture(gesture: TouchGesture, x: Float, y: Float)
        fun onScrollSpeedChange(newSpeed: Int)
    }
    
    enum class TouchGesture {
        SINGLE_TAP,
        DOUBLE_TAP,
        LONG_PRESS,
        SWIPE_UP,
        SWIPE_DOWN
    }
    
    private var listener: TouchScrollListener? = null
    
    fun setTouchScrollListener(listener: TouchScrollListener) {
        this.listener = listener
    }
    
    fun setTouchModeEnabled(enabled: Boolean) {
        isTouchModeEnabled = enabled
        if (!enabled) {
            stopTouchScrolling()
        }
        Log.d(TAG, "Touch mode ${if (enabled) "enabled" else "disabled"}")
    }
    
    fun isTouchModeEnabled(): Boolean = isTouchModeEnabled
    
    fun setTouchSensitivity(sensitivity: Float) {
        touchSensitivity = sensitivity.coerceIn(0.0f, 1.0f)
        Log.d(TAG, "Touch sensitivity set to $touchSensitivity")
    }
    
    fun getTouchSensitivity(): Float = touchSensitivity
    
    fun setScrollSpeed(speed: Int) {
        currentScrollSpeed = speed.coerceIn(1, 5)
    }
    
    fun setAdaptiveScrolling(enabled: Boolean) {
        adaptiveScrolling = enabled
    }
    
    fun setAutoResumeEnabled(enabled: Boolean) {
        autoResumeEnabled = enabled
    }
    
    /**
     * Process touch events from AccessibilityService
     */
    fun processTouchEvent(x: Float, y: Float, eventTime: Long) {
        if (!isTouchModeEnabled) return
        
        lastTouchPosition.set(x, y)
        val currentTime = System.currentTimeMillis()
        
        // Detect gesture type
        val gesture = detectGesture(x, y, currentTime, eventTime)
        
        when (gesture) {
            TouchGesture.SINGLE_TAP -> {
                handleSingleTap(x, y)
            }
            TouchGesture.DOUBLE_TAP -> {
                handleDoubleTap(x, y)
            }
            TouchGesture.LONG_PRESS -> {
                handleLongPress(x, y)
            }
            TouchGesture.SWIPE_UP -> {
                handleSwipeUp(x, y)
            }
            TouchGesture.SWIPE_DOWN -> {
                handleSwipeDown(x, y)
            }
        }
        
        listener?.onTouchGesture(gesture, x, y)
    }
    
    private fun detectGesture(x: Float, y: Float, currentTime: Long, eventTime: Long): TouchGesture {
        val timeSinceLastTouch = currentTime - lastTouchTime
        
        return when {
            timeSinceLastTouch < doubleTapThreshold && touchCount == 1 -> {
                touchCount = 0
                TouchGesture.DOUBLE_TAP
            }
            eventTime > longPressThreshold -> {
                TouchGesture.LONG_PRESS
            }
            abs(y - lastTouchPosition.y) > 100 -> {
                if (y < lastTouchPosition.y) TouchGesture.SWIPE_UP else TouchGesture.SWIPE_DOWN
            }
            else -> {
                touchCount++
                lastTouchTime = currentTime
                TouchGesture.SINGLE_TAP
            }
        }
    }
    
    private fun handleSingleTap(x: Float, y: Float) {
        Log.d(TAG, "Single tap detected at ($x, $y)")
        
        if (isScrollingFromTouch) {
            // If already scrolling, pause
            pauseTouchScrolling()
        } else {
            // Start scrolling from touch position
            startTouchScrolling(x, y)
        }
    }
    
    private fun handleDoubleTap(x: Float, y: Float) {
        Log.d(TAG, "Double tap detected at ($x, $y)")
        
        if (isScrollingFromTouch) {
            stopTouchScrolling()
        } else {
            // Quick start with adaptive speed
            startTouchScrolling(x, y, true)
        }
    }
    
    private fun handleLongPress(x: Float, y: Float) {
        Log.d(TAG, "Long press detected at ($x, $y)")
        
        // Cycle through speeds
        val newSpeed = if (currentScrollSpeed >= 5) 1 else currentScrollSpeed + 1
        currentScrollSpeed = newSpeed
        listener?.onScrollSpeedChange(newSpeed)
        
        // Restart scrolling with new speed if currently scrolling
        if (isScrollingFromTouch) {
            startTouchScrolling(x, y)
        }
    }
    
    private fun handleSwipeUp(x: Float, y: Float) {
        Log.d(TAG, "Swipe up detected")
        
        // Increase speed
        if (currentScrollSpeed < 5) {
            currentScrollSpeed++
            listener?.onScrollSpeedChange(currentScrollSpeed)
        }
    }
    
    private fun handleSwipeDown(x: Float, y: Float) {
        Log.d(TAG, "Swipe down detected")
        
        // Decrease speed
        if (currentScrollSpeed > 1) {
            currentScrollSpeed--
            listener?.onScrollSpeedChange(currentScrollSpeed)
        }
    }
    
    private fun startTouchScrolling(x: Float, y: Float, adaptive: Boolean = false) {
        stopTouchScrolling() // Stop any existing scrolling
        
        isScrollingFromTouch = true
        
        // Calculate optimal scroll parameters based on touch position
        val scrollDelay = calculateScrollDelay(y, adaptive)
        val scrollDistance = calculateScrollDistance(x, y)
        
        Log.d(TAG, "Starting touch scrolling from ($x, $y) with delay $scrollDelay")
        
        listener?.onTouchScrollStart(x, y)
        
        // Start scrolling with calculated parameters
        scheduleNextTouchScroll(scrollDelay, scrollDistance)
    }
    
    private fun pauseTouchScrolling() {
        touchScrollRunnable?.let { touchScrollHandler.removeCallbacks(it) }
        Log.d(TAG, "Touch scrolling paused")
    }
    
    private fun stopTouchScrolling() {
        isScrollingFromTouch = false
        touchScrollRunnable?.let { touchScrollHandler.removeCallbacks(it) }
        listener?.onTouchScrollStop()
        Log.d(TAG, "Touch scrolling stopped")
    }
    
    private fun scheduleNextTouchScroll(delay: Long, distance: Int) {
        if (!isScrollingFromTouch) return
        
        touchScrollRunnable = Runnable {
            // Perform scroll gesture
            performTouchScroll(distance)
            
            // Schedule next scroll if still active
            if (isScrollingFromTouch) {
                val nextDelay = if (adaptiveScrolling) {
                    calculateAdaptiveDelay(delay)
                } else {
                    delay
                }
                scheduleNextTouchScroll(nextDelay, distance)
            }
        }
        
        touchScrollHandler.postDelayed(touchScrollRunnable!!, delay)
    }
    
    private fun performTouchScroll(distance: Int) {
        // This will be called by the AccessibilityService to perform the actual scroll
        // The service will handle the gesture dispatch
    }
    
    private fun calculateScrollDelay(touchY: Float, adaptive: Boolean): Long {
        val baseDelay = when (currentScrollSpeed) {
            1 -> 3000L
            2 -> 2500L
            3 -> 2000L
            4 -> 1500L
            5 -> 1000L
            else -> 2000L
        }
        
        if (!adaptive) return baseDelay
        
        // Adaptive delay based on touch position and sensitivity
        val screenHeight = context.resources.displayMetrics.heightPixels
        val relativePosition = touchY / screenHeight
        
        // Slower scrolling for top of screen (beginning of content)
        // Faster scrolling for bottom of screen (user wants to catch up)
        val adaptiveFactor = 0.5f + (relativePosition * touchSensitivity)
        
        return (baseDelay * adaptiveFactor).toLong()
    }
    
    private fun calculateScrollDistance(touchX: Float, touchY: Float): Int {
        val density = context.resources.displayMetrics.density
        val baseDistance = (200 * density).toInt()
        
        // Adjust distance based on touch sensitivity
        return (baseDistance * (0.5f + touchSensitivity * 0.5f)).toInt()
    }
    
    private fun calculateAdaptiveDelay(currentDelay: Long): Long {
        // Gradually adjust delay based on scrolling patterns
        // This could be enhanced with machine learning in the future
        return currentDelay
    }
    
    /**
     * Check if user is actively interacting (to pause auto-scroll)
     */
    fun isUserInteracting(): Boolean {
        val timeSinceLastTouch = System.currentTimeMillis() - lastTouchTime
        return timeSinceLastTouch < 2000L // 2 seconds threshold
    }
    
    /**
     * Get current touch position for UI feedback
     */
    fun getLastTouchPosition(): PointF = PointF(lastTouchPosition.x, lastTouchPosition.y)
    
    /**
     * Resume scrolling from last position if auto-resume is enabled
     */
    fun resumeFromLastPosition() {
        if (autoResumeEnabled && !isScrollingFromTouch) {
            startTouchScrolling(lastTouchPosition.x, lastTouchPosition.y)
        }
    }
    
    companion object {
        private const val TAG = "TouchScrollManager"
    }
}