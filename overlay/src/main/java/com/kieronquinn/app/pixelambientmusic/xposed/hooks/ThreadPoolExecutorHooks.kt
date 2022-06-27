package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.media.MediaPlayer
import android.os.Build
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks
import java.util.concurrent.ThreadPoolExecutor

/**
 *  Blocks loading Force Stop runnable on Android 10 devices. This may cause some side effects, but
 *  is the only way to get it to load.
 */
class ThreadPoolExecutorHooks: XposedHooks() {

    override val clazz = ThreadPoolExecutor::class.java

    private fun execute(runnable: Runnable) = MethodHook {
        if(runnable.shouldBlock()) {
            return@MethodHook MethodResult.Replace<Unit>(null)
        }
        MethodResult.Skip()
    }

    private fun Any.shouldBlock(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.R && this::class.java.constructors.count {
            it.parameterTypes.contentEquals(
                arrayOf(MediaPlayer::class.java, String::class.java, Integer.TYPE)
            )
        } == 1
    }

}