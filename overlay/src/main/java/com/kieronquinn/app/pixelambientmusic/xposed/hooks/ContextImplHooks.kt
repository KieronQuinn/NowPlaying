package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

class ContextImplHooks: XposedHooks() {

    companion object {
        private const val PERMISSION_AMBIENT_INDICATION =
            "com.google.android.ambientindication.permission.AMBIENT_INDICATION"
    }

    override val clazz = Class.forName("android.app.ContextImpl")

    private fun checkSelfPermission(permission: String) = MethodHook {
        //Allow faked access to device config
        if(permission == "android.permission.READ_DEVICE_CONFIG") {
            return@MethodHook MethodResult.Replace(PackageManager.PERMISSION_GRANTED)
        }
        //Allow proxied access to Sensor Privacy on Android S+
        if(permission == "android.permission.OBSERVE_SENSOR_PRIVACY" && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return@MethodHook MethodResult.Replace(PackageManager.PERMISSION_GRANTED)
        }
        //Pretend we have AMBIENT_INDICATION, sendBroadcast will later be blocked
        if(permission == PERMISSION_AMBIENT_INDICATION) {
            return@MethodHook MethodResult.Replace(PackageManager.PERMISSION_GRANTED)
        }
        MethodResult.Skip()
    }

    private fun sendBroadcast(intent: Intent, permission: String) = MethodHook {
        if(permission == PERMISSION_AMBIENT_INDICATION){
            //This won't work, abort
            return@MethodHook MethodResult.Replace<Void>(null)
        }
        MethodResult.Skip()
    }

}