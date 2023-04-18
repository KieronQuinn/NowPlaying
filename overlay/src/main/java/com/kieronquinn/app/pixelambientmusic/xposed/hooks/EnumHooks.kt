package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.util.Log
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge

class EnumHooks: XposedHooks() {

    companion object {
        private val VALUES_RELEASES = setOf(
            "DEV", "FISHFOOD", "DOGFOOD", "DROIDFOOD", "PLAY_RELEASE", "SYSIMG_RELEASE", "UNKNOWN"
        )
    }

    override val clazz: Class<*> = Enum::class.java

    private fun constructor_enum(name: String, ordinal: Int) = MethodHook(afterHookedMethod = {
        if(VALUES_RELEASES.contains(name)){
            thisObject.hookDeveloperModeIfRequired()
        }
        MethodResult.Skip<Unit>()
    })

    private fun Any.hookDeveloperModeIfRequired() {
        val booleanMethodCount = this::class.java.methods.count {
            it.parameterCount == 0 && it.returnType == Boolean::class.java
        }
        if(booleanMethodCount != 2) return
        XposedBridge.hookMethod(
            //This should be stable as there are only two methods and this is the first
            this::class.java.getMethod("a"),
            object: XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam): Any {
                    param.result = true
                    return true
                }
            }
        )
    }

}