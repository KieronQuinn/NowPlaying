package com.kieronquinn.app.pixelambientmusic.utils.compat

import android.os.Build
import android.os.ext.SdkExtensions

object SdkExtensionsCompat {

    @JvmStatic
    fun getExtensionVersion(extension: Int): Int {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            SdkExtensions.getExtensionVersion(extension)
        }else 0
    }

}