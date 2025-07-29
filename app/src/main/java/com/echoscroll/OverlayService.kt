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
    private lateinit var currentSpeedText: TextView
    private lateinit var statusIndicator: TextView
    private lateinit var speedButtons: List<Button>

    private val scrollStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ScrollAccessibilityService.ACTION_SCROLL_STATE_CHANGED) {
                isScrolling = intent.getBooleanExtra(ScrollAccessibilityService.EXTRA_IS_SCROLLING, false)
                updateUI()
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
        val filter = IntentFilter(ScrollAccessibilityService.ACTION_SCROLL_STATE_CHANGED)
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
            currentSpeedText = view.findViewById(R.id.currentSpeedText)
            statusIndicator = view.findViewById(R.id.statusIndicator)
            
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
        statusIndicator.text = if (isScrolling) {
            getString(R.string.scrolling_active)
        } else {
            getString(R.string.scrolling_paused)
        }
        
        // Update button states
        btnStart.isEnabled = !isScrolling
        btnStop.isEnabled = isScrolling
        
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
}