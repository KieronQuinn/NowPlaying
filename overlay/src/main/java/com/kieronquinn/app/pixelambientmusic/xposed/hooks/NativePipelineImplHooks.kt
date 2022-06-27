package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.graphics.Bitmap
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Blocks native methods, faking results
 */
class NativePipelineImplHooks: XposedHooks() {

    companion object {
        private fun fakeNative() = MethodHook(beforeHookedMethod = {
            MethodResult.Replace(1L)
        })
    }

    override val clazz = Class.forName(
        "com.google.android.libraries.vision.visionkit.pipeline.NativePipelineImpl"
    )

    fun initialize(arg1: ByteArray?, arg2: Long, arg3: Long, arg4: Long, arg5: Long) = fakeNative()

    // lar
    fun initializeFrameBufferReleaseCallback(arg1: Long) = fakeNative()

    // lar
    fun initializeFrameManager() = fakeNative()

    // lar
    fun initializeResultsCallback() = fakeNative()

    // lar
    fun processBitmap(
        arg1: Long,
        arg2: Long,
        arg3: Bitmap?,
        arg4: Int,
        arg5: Int,
        arg6: Int,
        arg7: Int
    ) = MethodHook<ByteArray?>(beforeHookedMethod = {
        MethodResult.Replace(null)
    })

    // lar
    fun receiveProcessContext(arg1: Long, arg2: Long, arg3: ByteArray?) = MethodHook(beforeHookedMethod = {
        MethodResult.Replace(false)
    })

    // lar
    fun start(arg1: Long) = MethodHook(beforeHookedMethod = {
        MethodResult.Replace(Unit)
    })

}