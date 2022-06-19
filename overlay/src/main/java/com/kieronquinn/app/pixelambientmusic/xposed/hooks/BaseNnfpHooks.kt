package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.content.Context
import com.google.audio.ambientmusic.NnfpRecognizerCallback
import com.google.audio.ambientmusic.RecognitionResult
import com.kieronquinn.app.pixelambientmusic.Injector
import com.kieronquinn.app.pixelambientmusic.components.recogniser.MusicRecogniser
import com.kieronquinn.app.pixelambientmusic.model.RecognitionFailure
import com.kieronquinn.app.pixelambientmusic.model.RecognitionFailureReason
import com.kieronquinn.app.pixelambientmusic.model.RecognitionSource
import com.kieronquinn.app.pixelambientmusic.providers.LevelDbProvider
import com.kieronquinn.app.pixelambientmusic.utils.extensions.dumpToFile
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks
import java.io.File

abstract class BaseNnfpHooks(private val context: Context): XposedHooks() {

    private val pamDir = File(context.filesDir, LevelDbProvider.PAM_FOLDER).absolutePath

    private val fileMapping = mapOf(
        "/product/etc/ambient/matcher_tah.leveldb" to "$pamDir/matcher_tah.leveldb",
        "/system/etc/firmware/music_detector.sound_model" to "$pamDir/music_detector.sound_model",
        "/system/etc/firmware/music_detector.descriptor" to "$pamDir/music_detector.descriptor"
    )

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
        MethodResult.Skip<Long>()
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
        MethodResult.Skip()
    })


}