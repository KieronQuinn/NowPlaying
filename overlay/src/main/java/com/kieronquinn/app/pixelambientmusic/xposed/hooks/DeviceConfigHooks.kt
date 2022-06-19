package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.provider.DeviceConfig
import android.provider.DeviceConfig.OnPropertiesChangedListener
import com.kieronquinn.app.pixelambientmusic.config.DeviceConfigOverrides
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks
import java.util.concurrent.Executor

/**
 *  Android System Intelligence loads Phenotypes from Device Config, which requires the
 *  READ_DEVICE_CONFIG permission - which is not grantable. We first trick it into thinking it
 *  has the permission (so it doesn't just use the defaults), and then replace the calls to
 *  [DeviceConfig.getProperty] and [DeviceConfig.addOnPropertiesChangedListener] to our own
 *  implementation in [DeviceConfigOverrides].
 */
class DeviceConfigHooks : XposedHooks() {

    override val clazz = DeviceConfig::class.java

    private fun getProperty(namespace: String, key: String) = MethodHook {
        DeviceConfigOverrides.getValue(key)?.let { override ->
            return@MethodHook MethodResult.Replace(override)
        }
        MethodResult.Replace(null)
    }

    private fun addOnPropertiesChangedListener(
        namespace: String,
        executor: Executor,
        listener: OnPropertiesChangedListener
    ) = MethodHook {
        DeviceConfigOverrides.addListener(listener)
        MethodResult.Replace<Void>(null)
    }

}