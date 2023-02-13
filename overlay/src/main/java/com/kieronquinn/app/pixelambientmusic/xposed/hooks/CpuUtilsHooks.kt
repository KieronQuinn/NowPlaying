package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Blocks native methods, faking results
 */
class CpuUtilsHooks: XposedHooks() {

    override val clazz: Class<*> = Class.forName("com.google.android.apps.miphone.aiai.common.cpu.CpuUtils")
    private fun setAffinityToSpecifiedCores(a: Int, b: Int) = MethodHook(beforeHookedMethod = {
        MethodResult.Replace(0)
    })

}