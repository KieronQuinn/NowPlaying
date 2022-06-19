package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.content.Context
import android.media.MediaMetadata
import android.media.musicrecognition.MusicRecognitionManager
import android.media.musicrecognition.RecognitionRequest
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.audio.ambientmusic.OnDemandRecognitionResult
import com.kieronquinn.app.ambientmusicmod.IRecognitionCallback
import com.kieronquinn.app.pixelambientmusic.Injector
import com.kieronquinn.app.pixelambientmusic.components.recogniser.MusicRecogniser
import com.kieronquinn.app.pixelambientmusic.model.RecognitionFailure
import com.kieronquinn.app.pixelambientmusic.model.RecognitionFailureReason.MusicRecognitionError
import com.kieronquinn.app.pixelambientmusic.model.RecognitionResult
import com.kieronquinn.app.pixelambientmusic.model.RecognitionSource
import com.kieronquinn.app.pixelambientmusic.service.ServiceController
import com.kieronquinn.app.pixelambientmusic.utils.extensions.dumpToFile
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks
import java.util.concurrent.Executor

/**
 *  Hook calls to [MusicRecognitionManager] and redirect them via Shizuku to allow access as the
 *  required permission is system and role-protected.
 */
@RequiresApi(Build.VERSION_CODES.S)
class MusicRecognitionManagerHooks(private val context: Context): XposedHooks() {

    override val clazz = MusicRecognitionManager::class.java

    private fun beginStreamingSearch(
        request: RecognitionRequest,
        executor: Executor,
        recognitionCallback: MusicRecognitionManager.RecognitionCallback
    ) = MethodHook {
        MusicRecogniser.onStartRecognizing(RecognitionSource.ON_DEMAND)
        val callback = object: IRecognitionCallback.Stub() {
            override fun onRecognitionSucceeded(
                recognitionRequest: RecognitionRequest,
                result: MediaMetadata,
                extras: Bundle?
            ) {
                val resultBytes = extras?.getByteArray("EXTRA_SOUND_SEARCH_EARS_RESULT")
                if(resultBytes != null){
                    if(Injector.DEBUG){
                        context.dumpToFile("result_ondemand.bin", resultBytes)
                    }
                    val onDemandResult =
                        OnDemandRecognitionResult.OnDemandResult.parseFrom(resultBytes)
                    val track = onDemandResult.track.trackName
                    val artist = onDemandResult.track.artist
                    val googleId = onDemandResult.track.googleId
                    MusicRecogniser.onRecognitionComplete(
                        RecognitionResult(
                            track,
                            artist,
                            RecognitionSource.ON_DEMAND,
                            emptyArray(),
                            googleId,
                            null
                        )
                    )
                }
                executor.execute {
                    recognitionCallback.onRecognitionSucceeded(recognitionRequest, result, extras)
                }
            }

            override fun onAudioStreamClosed() {
                executor.execute {
                    recognitionCallback.onAudioStreamClosed()
                }
            }

            override fun onRecognitionFailed(
                recognitionRequest: RecognitionRequest,
                failureCode: Int
            ) {
                MusicRecogniser.onRecognitionFailed(
                    RecognitionFailure(
                        MusicRecognitionError(failureCode), RecognitionSource.ON_DEMAND, null
                    )
                )
                executor.execute {
                    recognitionCallback.onRecognitionFailed(recognitionRequest, failureCode)
                }
            }
        }
        ServiceController.runWithService { proxy ->
            proxy.MusicRecognitionManager_beginStreamingSearch(request, callback)
        }
        MethodResult.Replace<Void>(null)
    }

}