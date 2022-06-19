package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.os.Build
import com.kieronquinn.app.pixelambientmusic.xposed.InjectedHooks
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge

/**
 *  Hooks and neuters CloudApi on SDK < S
 */
object CloudApiHooks: InjectedHooks() {

    override val label = "com/google/intelligence/sense/ondemand/cloud/CloudApiImpl"

    private val shouldBlock = Build.VERSION.SDK_INT < Build.VERSION_CODES.S

    override fun custom(clazz: Class<*>) {
        //Disable constructor
        XposedBridge.hookMethod(
            clazz.constructors.first(),
            object: XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    super.beforeHookedMethod(param)
                    if(!shouldBlock) return
                    param.result = null
                }
            }
        )
        //Make isAvailable always return false
        XposedBridge.hookMethod(
            clazz.methods.first { it.returnType == Boolean::class.java },
            object: XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    super.beforeHookedMethod(param)
                    if(!shouldBlock) return
                    param.result = false
                }
            }
        )
    }

}