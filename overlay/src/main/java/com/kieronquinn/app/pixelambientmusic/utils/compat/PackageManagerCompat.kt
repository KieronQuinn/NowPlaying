package com.kieronquinn.app.pixelambientmusic.utils.compat

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build

@Suppress("DEPRECATION")
object PackageManagerCompat {

    @JvmStatic
    fun getApplicationInfo(
        packageManager: PackageManager,
        packageName: String,
        flags: ApplicationInfoFlags
    ): ApplicationInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getApplicationInfo(
                packageName, PackageManager.ApplicationInfoFlags.of(flags.flags)
            )
        } else {
            packageManager.getApplicationInfo(packageName, flags.flags.toInt())
        }
    }

    class ApplicationInfoFlags(val flags: Long) {
        companion object {
            @JvmStatic
            fun of(flags: Long): ApplicationInfoFlags {
                return ApplicationInfoFlags(flags)
            }
        }
    }

}