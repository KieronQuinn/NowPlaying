package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.content.Context
import com.google.audio.ambientmusic.NnfpRecognizer
import com.google.audio.ambientmusic.NnfpRecognizerCallback

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
        return super.recognize(id, audio, previousMatch, callback, runOnSmallCores)
    }

    override fun addOnDemandRecognizedTrack(
        pointer: Long,
        input: ByteArray
    ): MethodHook<ByteArray> {
        return super.addOnDemandRecognizedTrack(pointer, input)
    }

}