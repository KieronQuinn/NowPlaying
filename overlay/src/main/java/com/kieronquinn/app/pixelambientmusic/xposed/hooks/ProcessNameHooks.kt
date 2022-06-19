package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.app.Application
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Android System Intelligence contains a check in AiAiApplication for the process name, so we
 *  hook [Application.getProcessName] and make it return `com.google.android.as` so it can continue
 */
class ProcessNameHooks: XposedHooks() {

    override val clazz = Application::class.java

    private fun getProcessName() = MethodHook {
        MethodResult.Replace("com.google.android.as")
    }

}