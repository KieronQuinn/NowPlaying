package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import com.google.android.libraries.geller.portable.GellerStorageChangeListenerHandler
import com.google.android.libraries.geller.portable.callbacks.GellerLoggingCallback
import com.google.android.libraries.geller.portable.callbacks.GellerStorageOperationsCallback
import com.google.android.libraries.geller.portable.database.GellerDatabase
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Blocks native methods, faking results
 */
class GellerHooks: XposedHooks() {

    override val clazz: Class<*> =
        Class.forName("com.google.android.libraries.geller.portable.Geller")

    private fun nativeCreate(
        a: GellerStorageOperationsCallback,
        b: GellerStorageChangeListenerHandler,
        c: GellerLoggingCallback,
        d: ByteArray
    ) = MethodHook {
        MethodResult.Replace(0L)
    }

    fun nativeDelete(a: Long, b: Long, c: String?, d: ByteArray?) = MethodHook {
        MethodResult.Replace(0L)
    }

    fun nativePropagateDeletion(a: Long, b: Long, c: ByteArray?) = MethodHook {
        MethodResult.Replace(Unit)
    }

    fun nativeReadElements(
        a: Long,
        b: Long,
        c: String?,
        d: ByteArray?,
        e: ByteArray?
    ) = MethodHook {
        MethodResult.Replace(byteArrayOf())
    }

    fun nativeReadMetadata(
        a: Long,
        b: Long,
        c: String?,
        d: String?
    ) = MethodHook {
        MethodResult.Replace(emptyArray<String>())
    }

    fun nativeUpdate(a: Long, b: Long, c: ByteArray?)  = MethodHook {
        MethodResult.Replace(emptyArray<String>())
    }

}