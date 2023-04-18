package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

class EkhoMaintenanceHooks: XposedHooks() {

    override val clazz: Class<*> =
        Class.forName("com.google.android.libraries.assistant.trainingcache.bindings.EkhoMaintenance")

    fun nativeCleanUp(arg0: Long, arg1: ByteArray?) = MethodHook {
        MethodResult.Replace(byteArrayOf())
    }

    fun nativeClear(arg0: Long) = MethodHook {
        MethodResult.Replace(Unit)
    }

    fun nativeCreate() = MethodHook {
        MethodResult.Replace(0L)
    }

    fun nativeDestroy(arg0: Long) = MethodHook {
        MethodResult.Replace(Unit)
    }

    fun nativeGetCacheMetrics(arg0: Long) = MethodHook {
        MethodResult.Replace(byteArrayOf())
    }

    fun nativeInit(arg0: Long, arg1: ByteArray) = MethodHook {
        MethodResult.Replace(Unit)
    }

}