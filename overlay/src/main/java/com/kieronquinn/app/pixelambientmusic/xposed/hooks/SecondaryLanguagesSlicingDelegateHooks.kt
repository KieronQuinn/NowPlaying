package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.util.Log
import com.kieronquinn.app.pixelambientmusic.config.DeviceConfigOverrides
import com.kieronquinn.app.pixelambientmusic.xposed.InjectedHooks
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge

object SecondaryLanguagesSlicingDelegateHooks: InjectedHooks() {

    override val label =
        "com/google/intelligence/sense/ambientmusic/updater/SecondaryLanguagesSlicingDelegate"

    override fun custom(clazz: Class<*>) {
        XposedBridge.hookMethod(
            clazz.methods.first { it.returnType == List::class.java },
            object: XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    super.beforeHookedMethod(param)
                    param.result = DeviceConfigOverrides.getExtraLanguages()
                }
            }
        )
    }

}