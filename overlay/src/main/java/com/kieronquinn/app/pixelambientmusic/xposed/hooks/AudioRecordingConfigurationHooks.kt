package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.media.AudioRecordingConfiguration
import android.os.Process
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Fakes the client UID for recording apps. This isn't surfaced anywhere in AMM, so it doesn't
 *  matter - just that a client exists.
 */
class AudioRecordingConfigurationHooks: XposedHooks() {

    override val clazz = AudioRecordingConfiguration::class.java

    fun getClientUid() = MethodHook {
        MethodResult.Replace(Process.myUid())
    }

}