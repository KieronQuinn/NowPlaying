package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.content.ContentResolver
import android.provider.Settings
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Now Playing attempts to write to [Settings.Secure] from its settings UI, which won't work
 *  without the permission. As it is not _required_, rather than having the user grant the
 *  permission via ADB, we'll simply disable it.
 */
class SettingsHooks : XposedHooks() {

    override val clazz = Settings.Secure::class.java

    private fun putInt(contentResolver: ContentResolver, key: String, value: Int) = MethodHook {
        MethodResult.Replace(true) //Block
    }

}