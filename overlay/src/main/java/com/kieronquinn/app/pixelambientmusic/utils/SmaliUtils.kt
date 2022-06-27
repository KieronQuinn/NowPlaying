package com.kieronquinn.app.pixelambientmusic.utils

import android.os.Build
import android.view.View
import android.view.Window
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.view.WindowCompat

/**
 *  Various externally-accessed fields and methods for Smali modifications
 */
object SmaliUtils {

    @JvmField
    val ACTION_PRESS_AND_HOLD: AccessibilityNodeInfo.AccessibilityAction? = null

    @JvmField
    val ACTION_IME_ENTER: AccessibilityNodeInfo.AccessibilityAction? = null

    @JvmStatic
    fun setDecorFitsSystemWindows(window: Window){
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    @JvmStatic
    fun getStateDescription(view: View): CharSequence? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.stateDescription
        }else null
    }

    @JvmStatic
    fun setStateDescription(view: View, description: CharSequence?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.stateDescription = description
        }
    }

    @JvmStatic
    fun setStateDescription(accessibilityNodeInfo: AccessibilityNodeInfo, description: CharSequence?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            accessibilityNodeInfo.stateDescription = description
        }
    }

}