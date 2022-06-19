package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.os.UserHandle
import android.os.UserManager
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

class UserManagerHooks: XposedHooks() {

    override val clazz = UserManager::class.java

    private fun getUserProfiles() = MethodHook {
        MethodResult.Replace(emptyList<UserHandle>())
    }

}