package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.os.Build
import android.view.View
import android.view.WindowInsets
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Disables inset handling on Android < 11, since it uses getInsets which does not exist.
 */
class ViewHooks: XposedHooks() {

    override val clazz = View::class.java

    fun dispatchApplyWindowInsets(insets: WindowInsets) = MethodHook {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) return@MethodHook MethodResult.Skip()
        MethodResult.Replace(insets)
    }

}