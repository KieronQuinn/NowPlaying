package com.kieronquinn.app.pixelambientmusic.xposed

import android.content.Context
import android.os.Build
import android.util.Log
import com.kieronquinn.app.pixelambientmusic.xposed.Xposed.MethodHookParam
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.AlbumArtCheckBoxHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.ApplicationPackageManagerHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.AstreaHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.AudioRecordHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.AudioRecordingConfigurationHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.ComponentNameHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.ContextHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.ContextImplHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.CpuUtilsHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.DeviceConfigHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.EkhoMaintenanceHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.EkhoWriterHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.EnumHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.FileHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.GellerHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.HistoryActivityHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.InjectionHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.JobInfoHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.JobSchedulerHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.LangIdJniModelLessHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.LearningControllerJniHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.LevelDbHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.LoggingHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.MusicRecognitionManagerHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.NativePipelineImplHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.NnfpHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.Nnfpv3Hooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.NotificationHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.ProcessNameHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.RuntimeHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.SensorPrivacyHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.SettingsHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.ShortcutManagerHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.SoundTriggerHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.SqliteHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.ThreadPoolExecutorHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.UriBuilderHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.UriMatcherHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.UserManagerHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.ViewHooks
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
                ComponentNameHooks(),
                ContextHooks(),
                ContextImplHooks(),
                CpuUtilsHooks(),
                DeviceConfigHooks(),
                EkhoMaintenanceHooks(),
                EkhoWriterHooks(),
                EnumHooks(),
                FileHooks(context),
                GellerHooks(),
                JobSchedulerHooks(context),
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

    private fun hookMethod(hook: Method, member: Member) {
        Xposed.deoptimizeMethod(hook)
        Xposed.hookMethod(
            member,
            object: Xposed.MethodHook() {
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

    private fun replaceMethod(hook: Method, member: Member) {
        Xposed.deoptimizeMethod(hook)
        Xposed.hookMethod(
            member,
            object: Xposed.MethodHook() {
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
        val afterHookedMethod: (MethodHookParam.(result: Any?) -> MethodResult<T>)? = null,
        val beforeHookedMethod: (MethodHookParam.(obj: Any?) -> MethodResult<T>)? = null,
    )

    sealed class MethodResult<T> {
        data class Replace<T>(val value: T?): MethodResult<T>()
        class Skip<T>: MethodResult<T>()
    }

}