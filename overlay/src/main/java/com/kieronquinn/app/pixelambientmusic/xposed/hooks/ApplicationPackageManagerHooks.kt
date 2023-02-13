package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.kieronquinn.app.pixelambientmusic.utils.extensions.isPackageInstalled
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Fixes crash if GSA is not installed
 */
@SuppressLint("PrivateApi")
class ApplicationPackageManagerHooks(
    private val context: Context
): XposedHooks() {

    companion object {
        private const val PACKAGE_GSA = "com.google.android.googlequicksearchbox"
    }

    override val clazz: Class<*> = Class.forName("android.app.ApplicationPackageManager")

    private fun getComponentEnabledSetting(componentName: ComponentName) = MethodHook {
        if(componentName.packageName == PACKAGE_GSA && !isGsaInstalled()){
            return@MethodHook MethodResult.Replace(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        }
        MethodResult.Skip()
    }

    private fun isGsaInstalled(): Boolean {
        return context.packageManager.isPackageInstalled(PACKAGE_GSA)
    }

}