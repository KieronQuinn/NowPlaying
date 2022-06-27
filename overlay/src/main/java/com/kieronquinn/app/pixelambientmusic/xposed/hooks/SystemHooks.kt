package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.annotation.SuppressLint
import android.content.Context
import com.kieronquinn.app.pixelambientmusic.utils.extensions.isArmv7
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Disables loading of many system libraries on armv7 devices, as well as fixing loading the
 *  old libsense.so
 */
class SystemHooks(context: Context): XposedHooks() {

    companion object {
        private val DENYLIST = arrayOf(
            "deepclu_jni_aiai",
            "lang_id_jni_model_less_native",
            "aiai_vkp",
            "sense_nnfp_v3",
            "deepclu_jni_aiai"
        )
        private val DENYLIST_ARMV7 = arrayOf(
            "sense_nnfp_v3"
        )
    }

    private val libDir = context.applicationInfo.nativeLibraryDir

    override val clazz = System::class.java

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    fun loadLibrary(library: String) = MethodHook {
        if(library == "sense") {
            //Directly load the lib since System.load() fails lookup
            System.load("$libDir/libsense.so")
            return@MethodHook MethodResult.Replace(Unit)
        }
        if(library == "leveldbjni") {
            //Directly load the lib since System.load() fails lookup
            System.load("$libDir/libleveldbjni.so")
            return@MethodHook MethodResult.Replace(Unit)
        }
        if(DENYLIST.contains(library)){
            return@MethodHook MethodResult.Replace(Unit)
        }
        if(!isArmv7) return@MethodHook MethodResult.Skip()
        if(DENYLIST_ARMV7.contains(library)){
            return@MethodHook MethodResult.Replace(Unit)
        }
        MethodResult.Skip()
    }

}