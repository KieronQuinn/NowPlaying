package com.kieronquinn.app.pixelambientmusic.utils.extensions

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Build

fun PackageManager.isPackageInstalled(packageName: String): Boolean {
    return getPackageInfo(packageName) != null
}

@Suppress("DEPRECATION")
fun PackageManager.getPackageInfo(packageName: String): PackageInfo? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0L))
        } else {
            getPackageInfo(packageName, 0)
        }
    }catch (e: NameNotFoundException){
        null
    }
}