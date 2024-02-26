package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.hardware.SensorPrivacyManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.kieronquinn.app.pixelambientmusic.service.ServiceController
import com.kieronquinn.app.pixelambientmusic.utils.SensorPrivacyListenerWrapper
import com.kieronquinn.app.pixelambientmusic.xposed.Xposed
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge

/**
 *  Redirects Sensor Privacy checks and listeners via the Shizuku service which has the required
 *  `android.permission.OBSERVE_SENSOR_PRIVACY` permission.
 */
@RequiresApi(Build.VERSION_CODES.S)
class SensorPrivacyHooks: XposedHooks() {

    override val clazz = SensorPrivacyManager::class.java

    private fun isSensorPrivacyEnabled(sensor: Int) = MethodHook {
        val result = try {
            ServiceController.runWithService { proxy ->
                proxy.isMicrophoneDisabled
            }
        }catch (e: RuntimeException){
            //Service timeout
            false
        }
        MethodResult.Replace(result)
    }

    init {
        try {
            //SensorPrivacyListener is a hidden inner class so we have to find and hook manually
            Xposed.hookMethod(
                SensorPrivacyManager::class.java.getMethod(
                    "addSensorPrivacyListener",
                    Integer.TYPE,
                    SensorPrivacyListenerWrapper.SENSOR_PRIVACY_LISTENER_CLASS
                ),
                object : Xposed.MethodHook() {
                    override fun beforeHookedMethod(param: Xposed.MethodHookParam) {
                        val listener = param.args[1]!!
                        val wrappedListener = SensorPrivacyListenerWrapper(listener)
                        try {
                            ServiceController.runWithService {
                                //The listener is never removed so we don't need to store the ID
                                it.addMicrophoneDisabledListener(wrappedListener)
                            }
                        }catch (e: RuntimeException) {
                            //Service timeout
                        }
                        param.result = null
                    }
                }
            )
        }catch (e: Exception){
            Log.e(TAG, "Failed to hook SensorPrivacyManager", e)
        }
    }

}