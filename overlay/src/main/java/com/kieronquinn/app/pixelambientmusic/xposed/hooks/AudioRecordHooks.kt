package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.media.AudioAttributes
import android.media.AudioFormat
import com.kieronquinn.app.pixelambientmusic.utils.ProxyAudioRecord
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Replaces the creation of AudioRecord with our own [ProxyAudioRecord]. Since the original is
 *  created via Reflection, we have to hook [Class.getConstructor] and check the class name.
 *
 *  [ProxyAudioRecord] has a clone of the constructor that the original tries to use, which is
 *  hidden in the system SDK so cannot be overridden.
 */
class AudioRecordHooks: XposedHooks() {

    override val clazz = Class::class.java

    private fun getConstructor(classes: Array<Class<*>>) = MethodHook {
        val name = (thisObject as? Class<*>)?.name ?: return@MethodHook MethodResult.Skip()
        if (name == "android.media.AudioRecord") {
            //Redirect AudioRecord to our own proxy implementation
            return@MethodHook MethodResult.Replace(ProxyAudioRecord::class.java.getConstructor(
                AudioAttributes::class.java,
                AudioFormat::class.java,
                Integer.TYPE,
                Integer.TYPE
            ))
        }
        MethodResult.Skip()
    }

}