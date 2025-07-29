package com.echoscroll

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Path
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.MotionEvent
import com.echoscroll.utils.PermissionHelper

class ScrollAccessibilityService : AccessibilityService(), TouchScrollManager.TouchScrollListener {

    private var isScrolling = false
    private var scrollSpeed = 3 // Default speed level
    private var scrollHandler = Handler(Looper.getMainLooper())
    private var scrollRunnable: Runnable? = null
    private lateinit var permissionHelper: PermissionHelper
    private lateinit var touchScrollManager: TouchScrollManager
    private var isTouchModeEnabled = false

    private val scrollReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_START_SCROLL -> startScrolling()
                ACTION_STOP_SCROLL -> stopScrolling()
                ACTION_SET_SPEED -> {
                    scrollSpeed = intent.getIntExtra(EXTRA_SPEED, 3)
                    touchScrollManager.setScrollSpeed(scrollSpeed)
                    if (isScrolling) {
                        restartScrolling()
                    }
                }
                ACTION_TOGGLE_TOUCH_MODE -> {
                    isTouchModeEnabled = intent.getBooleanExtra(EXTRA_TOUCH_MODE, false)
                    touchScrollManager.setTouchModeEnabled(isTouchModeEnabled)
                }
                ACTION_SET_TOUCH_SENSITIVITY -> {
                    val sensitivity = intent.getFloatExtra(EXTRA_SENSITIVITY, 0.5f)
                    touchScrollManager.setTouchSensitivity(sensitivity)
                }
                ACTION_SET_HOLD_TO_SCROLL -> {
                    val holdToScrollEnabled = intent.getBooleanExtra(EXTRA_HOLD_TO_SCROLL, true)
                    touchScrollManager.setHoldToScrollEnabled(holdToScrollEnabled)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        permissionHelper = PermissionHelper(this)
        touchScrollManager = TouchScrollManager(this)
        touchScrollManager.setTouchScrollListener(this)
        registerScrollReceiver()
        Log.d(TAG, "ScrollAccessibilityService created")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "ScrollAccessibilityService connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let { 
            // Handle touch events for touch-to-scroll functionality
            when (event.eventType) {
                AccessibilityEvent.TYPE_TOUCH_INTERACTION_START -> {
                    if (isTouchModeEnabled) {
                        handleTouchDownEvent(event)
                    }
                }
                AccessibilityEvent.TYPE_TOUCH_INTERACTION_END -> {
                    if (isTouchModeEnabled) {
                        handleTouchUpEvent(event)
                    }
                }
                AccessibilityEvent.TYPE_GESTURE_DETECTION_START,
                AccessibilityEvent.TYPE_GESTURE_DETECTION_END -> {
                    if (isTouchModeEnabled) {
                        handleTouchEvent(event)
                    }
                }
            }
            
            // Auto-pause scrolling when user is actively interacting
            if (touchScrollManager.isUserInteracting() && isScrolling) {
                pauseForUserInteraction()
            }
        }
    }
    
    private fun handleTouchEvent(event: AccessibilityEvent) {
        try {
            // Extract touch coordinates from the event
            val source = event.source
            if (source != null) {
                val rect = android.graphics.Rect()
                source.getBoundsInScreen(rect)
                
                val x = rect.centerX().toFloat()
                val y = rect.centerY().toFloat()
                
                touchScrollManager.processTouchEvent(x, y, event.eventTime)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling touch event", e)
        }
    }
    
    private fun handleTouchDownEvent(event: AccessibilityEvent) {
        try {
            // Extract touch coordinates for touch down
            val source = event.source
            if (source != null) {
                val rect = android.graphics.Rect()
                source.getBoundsInScreen(rect)
                
                val x = rect.centerX().toFloat()
                val y = rect.centerY().toFloat()
                
                Log.d(TAG, "Touch down detected at ($x, $y)")
                touchScrollManager.processTouchDown(x, y)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling touch down event", e)
        }
    }
    
    private fun handleTouchUpEvent(event: AccessibilityEvent) {
        try {
            // Extract touch coordinates for touch up
            val source = event.source
            if (source != null) {
                val rect = android.graphics.Rect()
                source.getBoundsInScreen(rect)
                
                val x = rect.centerX().toFloat()
                val y = rect.centerY().toFloat()
                
                Log.d(TAG, "Touch up detected at ($x, $y)")
                touchScrollManager.processTouchUp(x, y)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling touch up event", e)
        }
    }
    
    private fun pauseForUserInteraction() {
        if (isScrolling) {
            stopScrolling()
            
            // Auto-resume after user stops interacting
            scrollHandler.postDelayed({
                if (!touchScrollManager.isUserInteracting()) {
                    touchScrollManager.resumeFromLastPosition()
                }
            }, 3000) // Resume after 3 seconds of inactivity
        }
    }
    
    // TouchScrollManager.TouchScrollListener implementation
    override fun onTouchScrollStart(x: Float, y: Float) {
        Log.d(TAG, "Touch scroll started at ($x, $y)")
        
        // Start scrolling from the touched position
        startScrolling()
        
        // Notify overlay service
        sendBroadcast(Intent(ACTION_TOUCH_SCROLL_START).apply {
            putExtra(EXTRA_TOUCH_X, x)
            putExtra(EXTRA_TOUCH_Y, y)
        })
    }
    
    override fun onTouchScrollStop() {
        Log.d(TAG, "Touch scroll stopped")
        stopScrolling()
    }
    
    override fun onTouchGesture(gesture: TouchScrollManager.TouchGesture, x: Float, y: Float) {
        Log.d(TAG, "Touch gesture: $gesture at ($x, $y)")
        
        // Notify overlay service about gesture
        sendBroadcast(Intent(ACTION_TOUCH_GESTURE).apply {
            putExtra(EXTRA_GESTURE_TYPE, gesture.name)
            putExtra(EXTRA_TOUCH_X, x)
            putExtra(EXTRA_TOUCH_Y, y)
        })
    }
    
    override fun onScrollSpeedChange(newSpeed: Int) {
        scrollSpeed = newSpeed
        Log.d(TAG, "Scroll speed changed to $newSpeed")
        
        // Notify overlay service
        sendBroadcast(Intent(ACTION_SET_SPEED).apply {
            putExtra(EXTRA_SPEED, newSpeed)
        })
    }

    override fun onInterrupt() {
        stopScrolling()
        Log.d(TAG, "ScrollAccessibilityService interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopScrolling()
        touchScrollManager.setTouchModeEnabled(false)
        unregisterReceiver(scrollReceiver)
        Log.d(TAG, "ScrollAccessibilityService destroyed")
    }

    private fun registerScrollReceiver() {
        val filter = IntentFilter().apply {
            addAction(ACTION_START_SCROLL)
            addAction(ACTION_STOP_SCROLL)
            addAction(ACTION_SET_SPEED)
            addAction(ACTION_TOGGLE_TOUCH_MODE)
            addAction(ACTION_SET_TOUCH_SENSITIVITY)
            addAction(ACTION_SET_HOLD_TO_SCROLL)
        }
        registerReceiver(scrollReceiver, filter)
    }

    private fun startScrolling() {
        if (isScrolling) return
        
        isScrolling = true
        scheduleNextScroll()
        Log.d(TAG, "Started scrolling at speed level $scrollSpeed")
        
        // Notify overlay service
        sendBroadcast(Intent(ACTION_SCROLL_STATE_CHANGED).apply {
            putExtra(EXTRA_IS_SCROLLING, true)
        })
    }

    private fun stopScrolling() {
        isScrolling = false
        scrollRunnable?.let { scrollHandler.removeCallbacks(it) }
        Log.d(TAG, "Stopped scrolling")
        
        // Notify overlay service
        sendBroadcast(Intent(ACTION_SCROLL_STATE_CHANGED).apply {
            putExtra(EXTRA_IS_SCROLLING, false)
        })
    }

    private fun restartScrolling() {
        if (!isScrolling) return
        
        scrollRunnable?.let { scrollHandler.removeCallbacks(it) }
        scheduleNextScroll()
    }

    private fun scheduleNextScroll() {
        if (!isScrolling) return
        
        val delay = permissionHelper.getSpeedDelay(scrollSpeed)
        
        scrollRunnable = Runnable {
            performScrollGesture()
            if (isScrolling) {
                scheduleNextScroll()
            }
        }
        
        scrollHandler.postDelayed(scrollRunnable!!, delay)
    }

    private fun performScrollGesture() {
        try {
            val displayMetrics = resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels
            
            // Create a scroll gesture from center to slightly up
            val startX = screenWidth / 2f
            val startY = screenHeight * 0.7f
            val endX = screenWidth / 2f
            val endY = screenHeight * 0.3f
            
            val path = Path().apply {
                moveTo(startX, startY)
                lineTo(endX, endY)
            }
            
            val gestureBuilder = GestureDescription.Builder()
            val strokeDescription = GestureDescription.StrokeDescription(path, 0, 300)
            gestureBuilder.addStroke(strokeDescription)
            
            val gesture = gestureBuilder.build()
            
            dispatchGesture(gesture, object : GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    Log.d(TAG, "Scroll gesture completed")
                }
                
                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    Log.w(TAG, "Scroll gesture cancelled")
                }
            }, null)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error performing scroll gesture", e)
        }
    }

    companion object {
        private const val TAG = "ScrollAccessibilityService"
        
        // Broadcast actions
        const val ACTION_START_SCROLL = "com.echoscroll.START_SCROLL"
        const val ACTION_STOP_SCROLL = "com.echoscroll.STOP_SCROLL"
        const val ACTION_SET_SPEED = "com.echoscroll.SET_SPEED"
        const val ACTION_TOGGLE_TOUCH_MODE = "com.echoscroll.TOGGLE_TOUCH_MODE"
        const val ACTION_SET_TOUCH_SENSITIVITY = "com.echoscroll.SET_TOUCH_SENSITIVITY"
        const val ACTION_SET_HOLD_TO_SCROLL = "com.echoscroll.SET_HOLD_TO_SCROLL"
        const val ACTION_SCROLL_STATE_CHANGED = "com.echoscroll.SCROLL_STATE_CHANGED"
        const val ACTION_TOUCH_SCROLL_START = "com.echoscroll.TOUCH_SCROLL_START"
        const val ACTION_TOUCH_GESTURE = "com.echoscroll.TOUCH_GESTURE"
        
        // Extras
        const val EXTRA_SPEED = "speed"
        const val EXTRA_IS_SCROLLING = "is_scrolling"
        const val EXTRA_TOUCH_MODE = "touch_mode"
        const val EXTRA_SENSITIVITY = "sensitivity"
        const val EXTRA_HOLD_TO_SCROLL = "hold_to_scroll"
        const val EXTRA_TOUCH_X = "touch_x"
        const val EXTRA_TOUCH_Y = "touch_y"
        const val EXTRA_GESTURE_TYPE = "gesture_type"
    }
}