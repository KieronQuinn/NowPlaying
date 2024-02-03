package com.kieronquinn.app.pixelambientmusic.utils.compat

import android.os.Build
import android.view.View
import android.view.WindowInsetsAnimation

object ViewCompat {

    @JvmStatic
    fun setWindowInsetsAnimationCallback(view: View, callback: WindowInsetsAnimation.Callback?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.setWindowInsetsAnimationCallback(callback)
        }
    }

}