package com.echoscroll

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.echoscroll.utils.PermissionHelper

class OverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var isScrolling = false
    private var currentSpeed = 3
    private lateinit var permissionHelper: PermissionHelper

    // UI Components
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var btnClose: Button
    private lateinit var btnTouchMode: Button
    private lateinit var btnSensitivityUp: Button
    private lateinit var btnSensitivityDown: Button
    private lateinit var currentSpeedText: TextView
    private lateinit var statusIndicator: TextView
    private lateinit var sensitivityText: TextView
    private lateinit var touchPositionIndicator: TextView
    private lateinit var gestureHelp: TextView
    private lateinit var touchSensitivityLayout: LinearLayout
    private lateinit var speedButtons: List<Button>
    
    // Touch mode state
    private var isTouchModeEnabled = false
    private var touchSensitivity = 0.5f

    private val scrollStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ScrollAccessibilityService.ACTION_SCROLL_STATE_CHANGED -> {
                    isScrolling = intent.getBooleanExtra(ScrollAccessibilityService.EXTRA_IS_SCROLLING, false)
                    updateUI()
                }
                ScrollAccessibilityService.ACTION_TOUCH_SCROLL_START -> {
                    val x = intent.getFloatExtra(ScrollAccessibilityService.EXTRA_TOUCH_X, 0f)
                    val y = intent.getFloatExtra(ScrollAccessibilityService.EXTRA_TOUCH_Y, 0f)
                    showTouchPosition(x, y)
                }
                ScrollAccessibilityService.ACTION_TOUCH_GESTURE -> {
                    val gestureType = intent.getStringExtra(ScrollAccessibilityService.EXTRA_GESTURE_TYPE)
                    val x = intent.getFloatExtra(ScrollAccessibilityService.EXTRA_TOUCH_X, 0f)
                    val y = intent.getFloatExtra(ScrollAccessibilityService.EXTRA_TOUCH_Y, 0f)
                    showGestureInfo(gestureType, x, y)
                }
                ScrollAccessibilityService.ACTION_SET_SPEED -> {
                    currentSpeed = intent.getIntExtra(ScrollAccessibilityService.EXTRA_SPEED, 3)
                    updateUI()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        permissionHelper = PermissionHelper(this)
        registerScrollStateReceiver()
        Log.d(TAG, "OverlayService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (overlayView == null) {
            createOverlay()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        removeOverlay()
        unregisterReceiver(scrollStateReceiver)
        Log.d(TAG, "OverlayService destroyed")
    }

    private fun registerScrollStateReceiver() {
        val filter = IntentFilter().apply {
            addAction(ScrollAccessibilityService.ACTION_SCROLL_STATE_CHANGED)
            addAction(ScrollAccessibilityService.ACTION_TOUCH_SCROLL_START)
            addAction(ScrollAccessibilityService.ACTION_TOUCH_GESTURE)
            addAction(ScrollAccessibilityService.ACTION_SET_SPEED)
        }
        registerReceiver(scrollStateReceiver, filter)
    }

    private fun createOverlay() {
        if (!permissionHelper.hasOverlayPermission()) {
            Log.w(TAG, "No overlay permission")
            return
        }

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)

        initOverlayViews()
        setupOverlayListeners()

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        try {
            windowManager?.addView(overlayView, layoutParams)
            updateUI()
            Log.d(TAG, "Overlay created successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating overlay", e)
        }
    }

    private fun initOverlayViews() {
        overlayView?.let { view ->
            btnStart = view.findViewById(R.id.btnStart)
            btnStop = view.findViewById(R.id.btnStop)
            btnClose = view.findViewById(R.id.btnClose)
            btnTouchMode = view.findViewById(R.id.btnTouchMode)
            btnSensitivityUp = view.findViewById(R.id.btnSensitivityUp)
            btnSensitivityDown = view.findViewById(R.id.btnSensitivityDown)
            currentSpeedText = view.findViewById(R.id.currentSpeedText)
            statusIndicator = view.findViewById(R.id.statusIndicator)
            sensitivityText = view.findViewById(R.id.sensitivityText)
            touchPositionIndicator = view.findViewById(R.id.touchPositionIndicator)
            gestureHelp = view.findViewById(R.id.gestureHelp)
            touchSensitivityLayout = view.findViewById(R.id.touchSensitivityLayout)
            
            speedButtons = listOf(
                view.findViewById(R.id.btnSpeed1),
                view.findViewById(R.id.btnSpeed2),
                view.findViewById(R.id.btnSpeed3),
                view.findViewById(R.id.btnSpeed4),
                view.findViewById(R.id.btnSpeed5)
            )
        }
    }

    private fun setupOverlayListeners() {
        // Start/Stop buttons
        btnStart.setOnClickListener {
            startScrolling()
        }

        btnStop.setOnClickListener {
            stopScrolling()
        }

        btnClose.setOnClickListener {
            stopSelf()
        }
        
        // Touch mode toggle
        btnTouchMode.setOnClickListener {
            toggleTouchMode()
        }
        
        // Sensitivity controls
        btnSensitivityUp.setOnClickListener {
            adjustSensitivity(0.1f)
        }
        
        btnSensitivityDown.setOnClickListener {
            adjustSensitivity(-0.1f)
        }

        // Speed buttons
        speedButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                setSpeed(index + 1)
            }
        }

        // Make overlay draggable
        setupDragListener()
    }

    private fun setupDragListener() {
        val dragHandle = overlayView?.findViewById<View>(R.id.dragHandle)
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        dragHandle?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val layoutParams = overlayView?.layoutParams as WindowManager.LayoutParams
                    initialX = layoutParams.x
                    initialY = layoutParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val layoutParams = overlayView?.layoutParams as WindowManager.LayoutParams
                    layoutParams.x = initialX + (event.rawX - initialTouchX).toInt()
                    layoutParams.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager?.updateViewLayout(overlayView, layoutParams)
                    true
                }
                else -> false
            }
        }
    }

    private fun startScrolling() {
        if (!permissionHelper.isAccessibilityServiceEnabled()) {
            Toast.makeText(this, "Please enable Accessibility Service first", Toast.LENGTH_LONG).show()
            return
        }

        sendBroadcast(Intent(ScrollAccessibilityService.ACTION_START_SCROLL))
        Log.d(TAG, "Start scrolling requested")
    }

    private fun stopScrolling() {
        sendBroadcast(Intent(ScrollAccessibilityService.ACTION_STOP_SCROLL))
        Log.d(TAG, "Stop scrolling requested")
    }
    
    private fun toggleTouchMode() {
        isTouchModeEnabled = !isTouchModeEnabled
        
        // Send touch mode state to accessibility service
        sendBroadcast(Intent(ScrollAccessibilityService.ACTION_TOGGLE_TOUCH_MODE).apply {
            putExtra(ScrollAccessibilityService.EXTRA_TOUCH_MODE, isTouchModeEnabled)
        })
        
        updateUI()
        Log.d(TAG, "Touch mode ${if (isTouchModeEnabled) "enabled" else "disabled"}")
    }
    
    private fun adjustSensitivity(delta: Float) {
        touchSensitivity = (touchSensitivity + delta).coerceIn(0.0f, 1.0f)
        
        // Send sensitivity to accessibility service
        sendBroadcast(Intent(ScrollAccessibilityService.ACTION_SET_TOUCH_SENSITIVITY).apply {
            putExtra(ScrollAccessibilityService.EXTRA_SENSITIVITY, touchSensitivity)
        })
        
        updateUI()
        Log.d(TAG, "Touch sensitivity adjusted to $touchSensitivity")
    }
    
    private fun showTouchPosition(x: Float, y: Float) {
        touchPositionIndicator.text = "Touch: (${x.toInt()}, ${y.toInt()})"
        touchPositionIndicator.visibility = View.VISIBLE
        
        // Hide after 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            touchPositionIndicator.visibility = View.GONE
        }, 2000)
    }
    
    private fun showGestureInfo(gestureType: String?, x: Float, y: Float) {
        gestureType?.let { gesture ->
            val gestureText = when (gesture) {
                "SINGLE_TAP" -> "👆 Tap"
                "DOUBLE_TAP" -> "👆👆 Double-tap"
                "LONG_PRESS" -> "👆⏱️ Long-press"
                "SWIPE_UP" -> "👆⬆️ Swipe up"
                "SWIPE_DOWN" -> "👆⬇️ Swipe down"
                else -> gesture
            }
            
            statusIndicator.text = "$gestureText at (${x.toInt()}, ${y.toInt()})"
            
            // Reset status after 1 second
            Handler(Looper.getMainLooper()).postDelayed({
                updateUI()
            }, 1000)
        }
    }

    private fun setSpeed(speed: Int) {
        currentSpeed = speed
        sendBroadcast(Intent(ScrollAccessibilityService.ACTION_SET_SPEED).apply {
            putExtra(ScrollAccessibilityService.EXTRA_SPEED, speed)
        })
        updateUI()
        Log.d(TAG, "Speed set to $speed")
    }

    private fun updateUI() {
        // Update speed display
        currentSpeedText.text = permissionHelper.getSpeedDescription(currentSpeed)
        
        // Update status
        statusIndicator.text = when {
            isTouchModeEnabled && isScrolling -> "Touch scrolling active"
            isTouchModeEnabled -> "Touch mode ready"
            isScrolling -> getString(R.string.scrolling_active)
            else -> getString(R.string.scrolling_paused)
        }
        
        // Update button states
        btnStart.isEnabled = !isScrolling && !isTouchModeEnabled
        btnStop.isEnabled = isScrolling
        
        // Update touch mode button
        btnTouchMode.text = if (isTouchModeEnabled) "ON" else "OFF"
        btnTouchMode.setBackgroundColor(
            if (isTouchModeEnabled) getColor(R.color.button_start) else getColor(R.color.error)
        )
        
        // Show/hide touch mode controls
        touchSensitivityLayout.visibility = if (isTouchModeEnabled) View.VISIBLE else View.GONE
        gestureHelp.visibility = if (isTouchModeEnabled) View.VISIBLE else View.GONE
        
        // Update sensitivity display
        sensitivityText.text = "${(touchSensitivity * 100).toInt()}%"
        
        // Highlight current speed button
        speedButtons.forEachIndexed { index, button ->
            button.alpha = if (index + 1 == currentSpeed) 1.0f else 0.6f
        }
    }

    private fun removeOverlay() {
        overlayView?.let { view ->
            try {
                windowManager?.removeView(view)
                overlayView = null
                Log.d(TAG, "Overlay removed")
            } catch (e: Exception) {
                Log.e(TAG, "Error removing overlay", e)
            }
        }
    }

    companion object {
        private const val TAG = "OverlayService"
    }
    
    private fun getColor(colorRes: Int): Int {
        return resources.getColor(colorRes, null)
    }
}