package com.echoscroll

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.echoscroll.utils.PermissionHelper

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var btnSetupAccessibility: Button
    private lateinit var btnSetupOverlay: Button
    private lateinit var btnStartService: Button
    private lateinit var btnStopService: Button

    private lateinit var permissionHelper: PermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        permissionHelper = PermissionHelper(this)
        setupClickListeners()
        updateUI()
    }

    private fun initViews() {
        statusText = findViewById(R.id.statusText)
        btnSetupAccessibility = findViewById(R.id.btnSetupAccessibility)
        btnSetupOverlay = findViewById(R.id.btnSetupOverlay)
        btnStartService = findViewById(R.id.btnStartService)
        btnStopService = findViewById(R.id.btnStopService)
    }

    private fun setupClickListeners() {
        btnSetupAccessibility.setOnClickListener {
            openAccessibilitySettings()
        }

        btnSetupOverlay.setOnClickListener {
            requestOverlayPermission()
        }

        btnStartService.setOnClickListener {
            startEchoScrollService()
        }

        btnStopService.setOnClickListener {
            stopEchoScrollService()
        }
    }

    private fun openAccessibilitySettings() {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
            Toast.makeText(this, "Please enable Echo Scroll in Accessibility Services", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to open accessibility settings", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestOverlayPermission() {
        if (!permissionHelper.hasOverlayPermission()) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
        } else {
            Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startEchoScrollService() {
        if (!permissionHelper.hasOverlayPermission()) {
            Toast.makeText(this, getString(R.string.overlay_permission_required), Toast.LENGTH_LONG).show()
            return
        }

        if (!permissionHelper.isAccessibilityServiceEnabled()) {
            Toast.makeText(this, getString(R.string.accessibility_permission_required), Toast.LENGTH_LONG).show()
            return
        }

        // Start the overlay service
        val overlayIntent = Intent(this, OverlayService::class.java)
        startService(overlayIntent)

        // Start the foreground service
        val foregroundIntent = Intent(this, ScrollForegroundService::class.java)
        startForegroundService(foregroundIntent)

        Toast.makeText(this, "Echo Scroll started!", Toast.LENGTH_SHORT).show()
        updateUI()
    }

    private fun stopEchoScrollService() {
        // Stop overlay service
        val overlayIntent = Intent(this, OverlayService::class.java)
        stopService(overlayIntent)

        // Stop foreground service
        val foregroundIntent = Intent(this, ScrollForegroundService::class.java)
        stopService(foregroundIntent)

        Toast.makeText(this, "Echo Scroll stopped!", Toast.LENGTH_SHORT).show()
        updateUI()
    }

    private fun updateUI() {
        val hasOverlay = permissionHelper.hasOverlayPermission()
        val hasAccessibility = permissionHelper.isAccessibilityServiceEnabled()

        // Update button states
        btnSetupOverlay.text = if (hasOverlay) "✓ Overlay Permission" else getString(R.string.setup_overlay)
        btnSetupAccessibility.text = if (hasAccessibility) "✓ Accessibility Service" else getString(R.string.setup_accessibility)

        // Update status
        when {
            !hasOverlay || !hasAccessibility -> {
                statusText.text = "Setup required"
                statusText.setTextColor(getColor(R.color.error))
            }
            else -> {
                statusText.text = getString(R.string.service_ready)
                statusText.setTextColor(getColor(R.color.button_start))
            }
        }

        // Enable/disable service buttons
        val canStart = hasOverlay && hasAccessibility
        btnStartService.isEnabled = canStart
        btnStopService.isEnabled = true
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (permissionHelper.hasOverlayPermission()) {
                Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
            }
            updateUI()
        }
    }

    companion object {
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1001
    }
}