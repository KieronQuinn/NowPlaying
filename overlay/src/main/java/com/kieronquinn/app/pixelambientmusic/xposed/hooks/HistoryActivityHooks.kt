package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.app.Activity
import android.content.Context
import android.os.Build
import com.google.intelligence.sense.ambientmusic.history.HistoryActivity
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Fixes insets on Android < 11
 */
class HistoryActivityHooks: XposedHooks() {

    override val clazz = HistoryActivity::class.java

    fun onResume() = MethodHook(afterHookedMethod = {
        val activity = thisObject as Activity
        activity.applyWindowInsets()
        MethodResult.Skip<Unit>()
    })

    private fun Activity.applyWindowInsets() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) return
        window.decorView.setPadding(
            0,
            getStatusBarHeight(),
            0,
            getNavigationBarHeight()
        )
    }

    private fun Context.getNavigationBarHeight(): Int {
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    private fun Context.getStatusBarHeight(): Int {
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

}