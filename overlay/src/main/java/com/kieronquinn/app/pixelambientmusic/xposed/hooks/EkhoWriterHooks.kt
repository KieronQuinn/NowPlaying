package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Blocks native methods, faking results
 */
class EkhoWriterHooks: XposedHooks() {

    override val clazz: Class<*> =
        Class.forName("com.google.android.libraries.assistant.trainingcache.bindings.EkhoWriter")

    private fun nativeCreate() = MethodHook {
        MethodResult.Replace(0L)
    }

    private fun nativeDestroy(token: Long) = MethodHook {
        MethodResult.Replace(Unit)
    }

    fun nativeEnableWriting(token: Long) = MethodHook {
        MethodResult.Replace(Unit)
    }

    fun nativeInit(token: Long, data: ByteArray?) = MethodHook {
        MethodResult.Replace(Unit)
    }

}