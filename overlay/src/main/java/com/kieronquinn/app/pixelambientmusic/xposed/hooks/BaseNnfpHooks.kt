package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.content.Context
import com.google.audio.ambientmusic.LegacyRecognitionResult
import com.google.audio.ambientmusic.NnfpRecognizer
import com.google.audio.ambientmusic.NnfpRecognizerCallback
import com.google.audio.ambientmusic.RecognitionResult
import com.kieronquinn.app.pixelambientmusic.Injector
import com.kieronquinn.app.pixelambientmusic.components.legacy.LegacyRecognitionConverter
import com.kieronquinn.app.pixelambientmusic.components.recogniser.MusicRecogniser
import com.kieronquinn.app.pixelambientmusic.model.RecognitionFailure
import com.kieronquinn.app.pixelambientmusic.model.RecognitionFailureReason
import com.kieronquinn.app.pixelambientmusic.model.RecognitionSource
import com.kieronquinn.app.pixelambientmusic.providers.LevelDbProvider
import com.kieronquinn.app.pixelambientmusic.utils.extensions.dumpToFile
import com.kieronquinn.app.pixelambientmusic.utils.extensions.isArmv7
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks
import java.io.File

abstract class BaseNnfpHooks(private val context: Context): XposedHooks() {

    private val pamDir = File(context.filesDir, LevelDbProvider.PAM_FOLDER).absolutePath

    private val fileMapping = mapOf(
        "/product/etc/ambient/matcher_tah.leveldb" to "$pamDir/matcher_tah.leveldb",
        "/system/etc/firmware/music_detector.sound_model" to "$pamDir/music_detector.sound_model",
        "/system/etc/firmware/music_detector.descriptor" to "$pamDir/music_detector.descriptor"
    )

    private val armv7Config by lazy {
        context.assets.open("config_armv7.bin").readBytes()
    }

    open fun init(
        shardNames: Array<String>,
        shardPaths: Array<String>,
        recogniserConfig: ByteArray
    ) = MethodHook({
        if(Injector.DEBUG) {
            context.dumpToFile("input.bin", recogniserConfig)
        }
        MethodResult.Skip()
    },{
        //Fix paths, removing system requirement
        val fixedPaths = shardPaths.clone()
        fixedPaths.forEachIndexed { index, shard ->
            fileMapping[shard]?.let { replacement ->
                fixedPaths[index] = replacement
            }
        }
        args[1] = fixedPaths
        if(isArmv7) {
            LegacyRecognitionConverter.setLastShardPaths(fixedPaths)
            //Use the old - static - config from the assets as the format has changed
            MethodResult.Replace(initArmv7(fixedPaths, armv7Config))
        }else {
            MethodResult.Skip()
        }
    })

    open fun addOnDemandRecognizedTrack(
        pointer: Long,
        input: ByteArray
    ) = MethodHook({
        val resultData = it as ByteArray
        if(Injector.DEBUG) {
            context.dumpToFile("on_demand_add_input.bin", input)
            context.dumpToFile("on_demand_add_result.bin", resultData)
        }
        MethodResult.Skip<ByteArray>()
    })

    open fun recognize(
        id: Long,
        audio: ShortArray,
        previousMatch: ByteArray,
        callback: NnfpRecognizerCallback,
        runOnSmallCores: Boolean
    ) = MethodHook({
        val resultData = it as ByteArray
        if(Injector.DEBUG){
            context.dumpToFile("result.bin", resultData)
            context.dumpToFile("previousMatch.bin", previousMatch)
        }
        val result = RecognitionResult.Result.parseFrom(resultData)
        val recognitionResult = result.tracksList.firstOrNull { track -> track.isMatch }?.let { track ->
            com.kieronquinn.app.pixelambientmusic.model.RecognitionResult(
                track.metadata.trackName,
                track.metadata.artist,
                RecognitionSource.NNFP,
                track.metadata.playerList.map { it.url }.toTypedArray(),
                track.metadata.googleId,
                audio
            )
        }
        if(recognitionResult != null){
            MusicRecogniser.onRecognitionComplete(recognitionResult)
        }else{
            MusicRecogniser.onRecognitionFailed(
                RecognitionFailure(RecognitionFailureReason.NoMatch, RecognitionSource.NNFP, audio)
            )
        }
        MethodResult.Skip<ByteArray?>()
    }, {
        MusicRecogniser.onStartRecognizing(RecognitionSource.NNFP)
        if(isArmv7) {
            val result = recogniseArmv7(id, audio, audio.size)
            val legacyResult = LegacyRecognitionResult.LegacyResult.parseFrom(result)
            val converted = LegacyRecognitionConverter.convertResult(legacyResult)
            MethodResult.Replace(converted.toByteArray())
        }else{
            MethodResult.Skip()
        }
    })

    private fun initArmv7(shardPaths: Array<String>, recogniserConfig: ByteArray): Long {
        return NnfpRecognizer::class.java.getDeclaredMethod(
            "init",
            Array<String>::class.java,
            ByteArray::class.java
        ).apply {
            isAccessible = true
        }.invoke(null, shardPaths, recogniserConfig) as Long
    }

    private fun recogniseArmv7(
        pointer: Long,
        audio: ShortArray,
        length: Int
    ): ByteArray {
        return NnfpRecognizer::class.java.getDeclaredMethod(
            "recognize",
            Long::class.java,
            ShortArray::class.java,
            Integer.TYPE,
            Integer.TYPE,
            Integer.TYPE
        ).apply {
            isAccessible = true
        }.invoke(null, pointer, audio, length, 16000, 5) as ByteArray
    }

}