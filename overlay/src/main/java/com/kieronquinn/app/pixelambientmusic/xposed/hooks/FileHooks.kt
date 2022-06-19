package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.annotation.SuppressLint
import android.content.Context
import com.kieronquinn.app.pixelambientmusic.providers.LevelDbProvider.Companion.PAM_FOLDER
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks
import java.io.File

/**
 *  Replaces File constructions for system files to point to internal dirs. This fixes all but one
 *  of the system file requirements - the last one requires a Smali edit.
 */
@SuppressLint("SoonBlockedPrivateApi")
class FileHooks(context: Context): XposedHooks() {

    private val pamDir = File(context.filesDir, PAM_FOLDER).absolutePath

    private val fileMapping = mapOf(
        "/product/etc/ambient/matcher_tah.leveldb" to "$pamDir/matcher_tah.leveldb",
        "/system/etc/firmware/music_detector.sound_model" to "$pamDir/music_detector.sound_model",
        "/system/etc/firmware/music_detector.descriptor" to "$pamDir/music_detector.descriptor"
    )

    override val clazz = File::class.java

    private fun constructor_path(input: String) = MethodHook{
        fileMapping[input]?.let { replacement ->
            args[0] = replacement
        }
        MethodResult.Skip<Void>()
    }

}