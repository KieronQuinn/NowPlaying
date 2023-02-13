package com.kieronquinn.app.pixelambientmusic.utils.compat

import android.graphics.Outline
import android.graphics.Path
import android.os.Build

object OutlineCompat {

    @JvmStatic
    fun setPath(outline: Outline, path: Path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            outline.setPath(path)
        }
    }

}