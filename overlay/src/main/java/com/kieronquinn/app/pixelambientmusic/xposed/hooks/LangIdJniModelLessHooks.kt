package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks
import java.nio.MappedByteBuffer

/**
 *  Blocks native calls, faking results
 */
class LangIdJniModelLessHooks: XposedHooks() {

    override val clazz = Class.forName("com.google.learning.expander.pod.inferenceapi.langid.LangIdJniModelLess")

    fun initJniWithModel(buffer: MappedByteBuffer) = MethodHook(beforeHookedMethod = {
        MethodResult.Replace(1L)
    })

}