package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.content.ComponentName
import android.util.Log
import com.kieronquinn.app.pixelambientmusic.BuildConfig
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

class ComponentNameHooks: XposedHooks() {

    companion object {
        private const val PACKAGE_NAME_AS = "com.google.android.as"
        private const val CLASS_NAME_HISTORY =
            "com.google.intelligence.sense.ambientmusic.history.HistoryActivity"
    }

    override val clazz = ComponentName::class.java

    private fun constructor_pname_class(packageName: String, clazz: String) = MethodHook {
        Log.d("CNH", "ComponentName: $packageName, $clazz")
        if(packageName == PACKAGE_NAME_AS && clazz == CLASS_NAME_HISTORY) {
            args[0] = BuildConfig.APPLICATION_ID
            Log.d("CNH", "Replacing package with ${BuildConfig.APPLICATION_ID}")
        }
        MethodResult.Skip<Unit>()
    }

}