package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.content.UriMatcher
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Now Playing uses Content Providers to handle history, which are hardcoded. It also checks the
 *  package name and assumes that anything that is not `com.google.android.as` is the old
 *  `com.google.intelligence.sense` package name, so we replace the URIs with our own ones.
 */
class UriMatcherHooks : XposedHooks() {

    override val clazz = UriMatcher::class.java

    private fun addURI(authority: String, path: String, code: Int) = MethodHook {
        //ASI automatically detects that we're not .as, and tries to use the old authority
        if (authority == "com.google.intelligence.sense.ambientmusic.historyprovider") {
            args[0] = "com.google.android.as.pam.ambientmusic.historyprovider"
        }
        MethodResult.Skip<Void>()
    }

}