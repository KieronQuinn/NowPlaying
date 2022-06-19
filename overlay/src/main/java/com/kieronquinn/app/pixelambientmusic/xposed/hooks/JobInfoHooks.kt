package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.app.job.JobInfo
import com.kieronquinn.app.pixelambientmusic.config.DeviceConfigOverrides
import com.kieronquinn.app.pixelambientmusic.utils.extensions.getComponent
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

class JobInfoHooks: XposedHooks() {

    companion object {
        private const val DEVICE_CONFIG_KEY_REQUIRE_WIFI = "Superpacks__require_wifi_by_default"
        private const val COMPONENT_SUPERPACKS =
            "com.google.android.apps.miphone.aiai.common.superpacks.impl.AiAiPersistentDownloadJobService"
    }

    override val clazz = JobInfo.Builder::class.java

    private fun setRequiredNetworkType(setRequiredNetworkType: Int) = MethodHook {
        val jobInfo = thisObject as JobInfo.Builder
        if(jobInfo.getComponent().className != COMPONENT_SUPERPACKS) {
            return@MethodHook MethodResult.Skip<JobInfo.Builder>()
        }
        DeviceConfigOverrides.getValue(DEVICE_CONFIG_KEY_REQUIRE_WIFI)?.let { requireWifi ->
            args[0] = if(requireWifi == "true") JobInfo.NETWORK_TYPE_UNMETERED else 0
        }
        MethodResult.Skip()
    }

}