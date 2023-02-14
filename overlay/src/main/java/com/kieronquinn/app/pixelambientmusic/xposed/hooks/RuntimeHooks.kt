package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.content.Context
import android.util.Log
import com.kieronquinn.app.pixelambientmusic.utils.extensions.isArmv7
import com.kieronquinn.app.pixelambientmusic.utils.extensions.isX86_64
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Disables loading of many system libraries on armv7 devices, as well as fixing loading the
 *  old libsense.so
 */
class RuntimeHooks(context: Context): XposedHooks() {

    companion object {
        private val DENYLIST = arrayOf(
            "deepclu_jni_aiai",
            "lang_id_jni_model_less_native",
            "aiai_vkp",
            "deepclu_jni_aiai",
            "geller_jni_lite_lib",
            "cpuutils",
            "modeleditor-jni",
            "dps_soda_pixel_s_jni",
        )
        private val DENYLIST_ARMV7 = arrayOf(
            "sense_nnfp_v3"
        )
    }

    override val clazz = Runtime::class.java

    fun loadLibrary0(loader: ClassLoader, caller: Class<*>, libName: String) = MethodHook {
        if(DENYLIST.contains(libName)){
            return@MethodHook MethodResult.Replace(Unit)
        }
        if(!isArmv7 && !isX86_64) return@MethodHook MethodResult.Skip()
        if(DENYLIST_ARMV7.contains(libName)){
            return@MethodHook MethodResult.Replace(Unit)
        }
        MethodResult.Skip()
    }

}