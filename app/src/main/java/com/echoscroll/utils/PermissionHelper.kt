package com.echoscroll.utils

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.view.accessibility.AccessibilityManager

class PermissionHelper(private val context: Context) {

    /**
     * Check if overlay permission is granted
     */
    fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true // Permission not required for older versions
        }
    }

    /**
     * Check if accessibility service is enabled
     */
    fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        
        val serviceName = "${context.packageName}/.ScrollAccessibilityService"
        
        return enabledServices.any { service ->
            service.id.contains(serviceName) || service.id.endsWith("ScrollAccessibilityService")
        }
    }

    /**
     * Get speed delay in milliseconds based on level (1-5)
     */
    fun getSpeedDelay(level: Int): Long {
        return when (level) {
            1 -> 3000L // Very slow
            2 -> 2500L // Slow
            3 -> 2000L // Normal
            4 -> 1500L // Fast
            5 -> 1000L // Faster
            else -> 2000L // Default to normal
        }
    }

    /**
     * Get speed description based on level
     */
    fun getSpeedDescription(level: Int): String {
        return when (level) {
            1 -> "Very Slow"
            2 -> "Slow"
            3 -> "Normal"
            4 -> "Fast"
            5 -> "Faster"
            else -> "Normal"
        }
    }

    /**
     * Check if device supports required features
     */
    fun isDeviceSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    /**
     * Get recommended scroll distance based on screen density
     */
    fun getScrollDistance(): Int {
        val density = context.resources.displayMetrics.density
        return (200 * density).toInt() // 200dp converted to pixels
    }
}