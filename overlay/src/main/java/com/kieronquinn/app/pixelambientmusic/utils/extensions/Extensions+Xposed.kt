package com.kieronquinn.app.pixelambientmusic.utils.extensions

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import java.lang.reflect.*

fun XC_MethodHook(beforeHookedMethod: ((param: XC_MethodHook.MethodHookParam) -> Unit)? = null, afterHookedMethod: ((param: XC_MethodHook.MethodHookParam) -> Unit)? = null): XC_MethodHook {
    return object: XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            beforeHookedMethod?.invoke(param)
            super.beforeHookedMethod(param)
        }

        override fun afterHookedMethod(param: MethodHookParam) {
            afterHookedMethod?.invoke(param)
            super.afterHookedMethod(param)
        }
    }
}

fun XC_MethodReplacement(replaceHookedMethod: ((param: XC_MethodHook.MethodHookParam) -> Any?)): XC_MethodReplacement {
    return object: XC_MethodReplacement() {
        override fun replaceHookedMethod(param: MethodHookParam): Any? {
            return replaceHookedMethod.invoke(param)
        }
    }
}

fun getCallingClassName(): String? {
    val classes = Thread.currentThread().stackTrace.map { it.className }
    val lspIndex = classes.indexOfFirst { it == "LSPHooker_" }
    if(lspIndex == -1 || lspIndex == classes.size) return null
    return classes[lspIndex + 1]
}