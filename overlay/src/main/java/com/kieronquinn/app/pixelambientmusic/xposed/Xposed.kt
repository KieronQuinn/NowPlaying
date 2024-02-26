package com.kieronquinn.app.pixelambientmusic.xposed

import android.os.Build
import de.robv.android.xposed.XC_MethodHook
import com.kieronquinn.app.pixelambientmusic.utils.pine.XC_MethodHook as PineXC_MethodHook
import com.kieronquinn.app.pixelambientmusic.utils.pine.XC_MethodHook.MethodHookParam as PineMethodHookParam
import com.kieronquinn.app.pixelambientmusic.utils.pine.XposedBridge as PineXposedBridge
import de.robv.android.xposed.XC_MethodHook as HookXC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam as HookMethodHookParam
import de.robv.android.xposed.XposedBridge as HookXposedBridge
import java.lang.reflect.Member
import java.lang.reflect.Method
import com.kieronquinn.app.pixelambientmusic.utils.pine.XC_MethodReplacement as PineXC_MethodReplacement
import de.robv.android.xposed.XC_MethodReplacement as HookXC_MethodReplacement

object Xposed {

    private fun isAndroid15(): Boolean {
        if(Build.VERSION.SDK_INT >= 35) return true
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                && Build.VERSION.PREVIEW_SDK_INT != 0
    }

    /**
     *  lsplant is not compatible with Android 15 and may never be due to Google stripping the
     *  symbols from libart. Pine is now used instead, although it is not currently compatible with
     *  x86_64.
     */
    private val USE_PINE = isAndroid15()

    fun hookMethod(replace: Member, hook: MethodHook) {
        if(USE_PINE) {
            PineXposedBridge.hookMethod(replace, object: PineXC_MethodHook() {
                override fun beforeHookedMethod(param: PineMethodHookParam) {
                    val localParam = MethodHookParam(param.thisObject, param.args, param.result)
                    return hook.beforeHookedMethod(localParam).also {
                        if(localParam.returnEarly) {
                            param.result = localParam.result
                        }
                    }
                }

                override fun afterHookedMethod(param: PineMethodHookParam) {
                    val localParam = MethodHookParam(param.thisObject, param.args, param.result)
                    return hook.afterHookedMethod(localParam).also {
                        if(localParam.returnEarly) {
                            param.result = localParam.result
                        }
                    }
                }
            })
        }else{
            HookXposedBridge.hookMethod(replace, object: HookXC_MethodHook() {
                override fun beforeHookedMethod(param: HookMethodHookParam) {
                    val localParam = MethodHookParam(param.thisObject, param.args, param.result)
                    return hook.beforeHookedMethod(localParam).also {
                        if(localParam.returnEarly) {
                            param.result = localParam.result
                        }
                    }
                }

                override fun afterHookedMethod(param: HookMethodHookParam) {
                    val localParam = MethodHookParam(param.thisObject, param.args, param.result)
                    return hook.afterHookedMethod(localParam).also {
                        if(localParam.returnEarly) {
                            param.result = localParam.result
                        }
                    }
                }
            })
        }
    }

    fun replaceMethod(replace: Member, replacement: MethodReplacement) {
        if(USE_PINE) {
            PineXposedBridge.hookMethod(replace, object: PineXC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam): Any? {
                    val localParam = MethodHookParam(param.thisObject, param.args, param.result)
                    return replacement.replaceHookedMethod(localParam).also {
                        param.result = localParam.result
                    }
                }
            })
        }else{
            HookXposedBridge.hookMethod(replace, object: HookXC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam): Any? {
                    val localParam = MethodHookParam(param.thisObject, param.args, param.result)
                    return replacement.replaceHookedMethod(localParam).also {
                        param.result = localParam.result
                    }
                }
            })
        }
    }

    fun deoptimizeMethod(method: Method) {
        if(USE_PINE) {
            PineXposedBridge.deoptimizeMethod(method)
        }else{
            HookXposedBridge.deoptimizeMethod(method)
        }
    }

    abstract class MethodHook {
        open fun beforeHookedMethod(param: MethodHookParam) {}
        open fun afterHookedMethod(param: MethodHookParam) {}
    }

    abstract class MethodReplacement {
        open fun replaceHookedMethod(param: MethodHookParam): Any? {
            return param.result
        }
    }

    class MethodHookParam(
        val thisObject: Any?,
        val args: Array<Any?>,
        var _result: Any? = null
    ) {
        var returnEarly: Boolean = false
        var result: Any?
            set(value) {
                _result = value
                returnEarly = true
            }
            get() = _result
    }
}