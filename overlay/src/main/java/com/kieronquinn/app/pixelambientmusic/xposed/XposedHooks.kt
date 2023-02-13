package com.kieronquinn.app.pixelambientmusic.xposed

import android.content.Context
import android.os.Build
import android.util.Log
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.*
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Member
import java.lang.reflect.Method

abstract class XposedHooks {

    companion object {
        internal const val TAG = "XposedHooks"

        fun setupHooks(context: Context) {
            arrayOf(
                AlbumArtCheckBoxHooks(context),
                ApplicationPackageManagerHooks(context),
                AstreaHooks(),
                AudioRecordHooks(),
                AudioRecordingConfigurationHooks(),
                ContextHooks(),
                ContextImplHooks(),
                CpuUtilsHooks(),
                DeviceConfigHooks(),
                EkhoWriterHooks(),
                FileHooks(context),
                GellerHooks(),
                JobSchedulerHooks(),
                JobInfoHooks(),
                HistoryActivityHooks(),
                InjectionHooks(),
                LangIdJniModelLessHooks(),
                LearningControllerJniHooks(),
                LevelDbHooks(),
                LoggingHooks(),
                NativePipelineImplHooks(),
                NnfpHooks(context),
                Nnfpv3Hooks(context),
                NotificationHooks(),
                ProcessNameHooks(),
                RuntimeHooks(context),
                SettingsHooks(),
                ShortcutManagerHooks(context),
                SqliteHooks(context),
                SoundTriggerHooks(),
                ThreadPoolExecutorHooks(),
                UserManagerHooks(),
                UriBuilderHooks(),
                UriMatcherHooks(),
                ViewHooks()
            ).forEach {
                it.init()
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                MusicRecognitionManagerHooks(context).init()
                SensorPrivacyHooks().init()
            }
        }
    }

    abstract val clazz: Class<*>?

    open fun init() {
        try {
            setupHooks()
        }catch (e: Exception){
            Log.e(TAG, "Error setting up hooks for ${clazz?.simpleName}", e)
        }
    }

    private fun setupHooks() {
        val clazz = clazz
        if(clazz == null){
            Log.e(TAG, "Failed to find clazz for ${this::class.java.simpleName} hooks")
            return
        }
        this::class.java.declaredMethods.forEach { hook ->
            //We only want method hooks
            if(hook.returnType != MethodHook::class.java) return@forEach
            hook.isAccessible = true
            val replace = when {
                hook.name.startsWith("any_") -> {
                    clazz.declaredMethods.firstOrNull {
                        it.parameterTypes.contentEquals(hook.parameterTypes)
                    } ?: run {
                        Log.e(TAG, "No target found for hook with parameter types " +
                                hook.parameterTypes.joinToString(", ") { it.name })
                        return@forEach
                    }
                }
                hook.name.startsWith("constructor_") -> {
                    clazz.getDeclaredConstructor(*hook.parameterTypes)
                }
                hook.name.startsWith("skip_") -> {
                    return@forEach
                }
                else -> {
                    clazz.getDeclaredMethod(hook.name, *hook.parameterTypes)
                }
            }
            hookMethod(hook, replace)
        }
    }

    private fun hookMethod(hook: Method, replace: Member) {
        XposedBridge.deoptimizeMethod(hook)
        XposedBridge.hookMethod(
            replace,
            object: XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val result = (hook.invoke(this@XposedHooks, *param.args) as MethodHook<*>)
                        .beforeHookedMethod?.invoke(param, param.thisObject)
                    if(result is MethodResult.Replace){
                        param.result = result.value
                    }
                }

                override fun afterHookedMethod(param: MethodHookParam) {
                    val result = (hook.invoke(this@XposedHooks, *param.args) as MethodHook<*>)
                        .afterHookedMethod?.invoke(param, param.result)
                    if(result is MethodResult.Replace){
                        param.result = result.value
                    }
                }
            }
        )
    }

    data class MethodHook<T>(
        val afterHookedMethod: (XC_MethodHook.MethodHookParam.(result: Any?) -> MethodResult<T>)? = null,
        val beforeHookedMethod: (XC_MethodHook.MethodHookParam.(obj: Any?) -> MethodResult<T>)? = null,
    )

    sealed class MethodResult<T> {
        data class Replace<T>(val value: T?): MethodResult<T>()
        class Skip<T>: MethodResult<T>()
    }
}