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
import com.echoscroll.utils.PermissionHelper

class ScrollAccessibilityService : AccessibilityService() {

    private var isScrolling = false
    private var scrollSpeed = 3 // Default speed level
    private var scrollHandler = Handler(Looper.getMainLooper())
    private var scrollRunnable: Runnable? = null
    private lateinit var permissionHelper: PermissionHelper

    private val scrollReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_START_SCROLL -> startScrolling()
                ACTION_STOP_SCROLL -> stopScrolling()
                ACTION_SET_SPEED -> {
                    scrollSpeed = intent.getIntExtra(EXTRA_SPEED, 3)
                    if (isScrolling) {
                        restartScrolling()
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        permissionHelper = PermissionHelper(this)
        registerScrollReceiver()
        Log.d(TAG, "ScrollAccessibilityService created")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "ScrollAccessibilityService connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // We don't need to handle accessibility events for our use case
        // The service is primarily used for gesture performance
    }

    override fun onInterrupt() {
        stopScrolling()
        Log.d(TAG, "ScrollAccessibilityService interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopScrolling()
        unregisterReceiver(scrollReceiver)
        Log.d(TAG, "ScrollAccessibilityService destroyed")
    }

    private fun registerScrollReceiver() {
        val filter = IntentFilter().apply {
            addAction(ACTION_START_SCROLL)
            addAction(ACTION_STOP_SCROLL)
            addAction(ACTION_SET_SPEED)
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
        const val ACTION_SCROLL_STATE_CHANGED = "com.echoscroll.SCROLL_STATE_CHANGED"
        
        // Extras
        const val EXTRA_SPEED = "speed"
        const val EXTRA_IS_SCROLLING = "is_scrolling"
    }
}