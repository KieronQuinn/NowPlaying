package com.kieronquinn.app.pixelambientmusic.utils.compat

import android.content.pm.ShortcutInfo
import android.os.Build

object ShortcutInfoCompat {

    @JvmStatic
    fun setExcludedFromSurfaces(builder: ShortcutInfo.Builder, surfaces: Int): ShortcutInfo.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            builder.setExcludedFromSurfaces(surfaces)
        }
        return builder
    }

}