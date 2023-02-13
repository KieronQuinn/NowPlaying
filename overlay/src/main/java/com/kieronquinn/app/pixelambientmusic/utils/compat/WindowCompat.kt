package com.kieronquinn.app.pixelambientmusic.utils.compat

import android.os.Build
import android.view.Window
import androidx.core.view.WindowCompat

object WindowCompat {

    @JvmStatic
    fun setDecorFitsSystemWindows(window: Window, fitsSystemWindows: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Prevent recursion as we've replaced the 31 impl
            window.setDecorFitsSystemWindows(fitsSystemWindows)
        }else{
            WindowCompat.setDecorFitsSystemWindows(window, fitsSystemWindows)
        }
    }

}