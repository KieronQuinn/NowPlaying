package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.content.Context
import android.util.Log
import com.google.audio.ambientmusic.NnfpRecognizer
import com.google.audio.ambientmusic.NnfpRecognizerCallback
import com.kieronquinn.app.pixelambientmusic.config.DeviceConfigOverrides

class NnfpHooks(context: Context): BaseNnfpHooks(context) {
    override val clazz = NnfpRecognizer::class.java

    override fun init(
        shardNames: Array<String>,
        shardPaths: Array<String>,
        recogniserConfig: ByteArray
    ): MethodHook<Long> {
        return super.init(shardNames, shardPaths, recogniserConfig)
    }

    override fun recognize(
        id: Long,
        audio: ShortArray,
        previousMatch: ByteArray,
        callback: NnfpRecognizerCallback,
        runOnSmallCores: Boolean
    ): MethodHook<ByteArray?> {
        if(DeviceConfigOverrides.isLoggingEnabled()){
            Log.d("NNFP", "Recognising using NNFP")
        }
        return super.recognize(id, audio, previousMatch, callback, runOnSmallCores)
    }

    override fun addOnDemandRecognizedTrack(
        pointer: Long,
        input: ByteArray
    ): MethodHook<ByteArray> {
        return super.addOnDemandRecognizedTrack(pointer, input)
    }

}