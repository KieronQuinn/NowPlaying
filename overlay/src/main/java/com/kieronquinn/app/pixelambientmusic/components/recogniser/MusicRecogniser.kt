package com.kieronquinn.app.pixelambientmusic.components.recogniser

import android.os.RemoteException
import com.kieronquinn.app.pixelambientmusic.IRecognitionCallback
import com.kieronquinn.app.pixelambientmusic.model.*
import java.util.*

/**
 *  Wrapper for interfacing with and responding to NnfpRecognizer & MusicRecognitionManager events.
 *
 *  Many events come in/out of the Xposed hooks, so this class aims to centralise listeners and
 *  calls.
 */
object MusicRecogniser {

    private const val METADATA_TIMEOUT = 500L
    private val callbacks = HashMap<String, Callback>()
    private var runningSource: RecognitionSource? = null
    private var state: State = State.IDLE
    private var lastMetadata: RecognitionMetadata? = null

    fun addCallback(callback: IRecognitionCallback, metadata: RecognitionCallbackMetadata): String {
        val id = UUID.randomUUID().toString()
        callbacks[id] = Callback(callback, metadata)
        return id.also {
            when(state) {
                State.RECORDING -> {
                    callback.onRecordingStarted()
                }
                State.RECOGNIZING -> {
                    callback.onRecognitionStarted()
                }
                State.IDLE -> {
                    //No-op
                }
            }
        }
    }

    fun removeCallback(id: String): Boolean {
        if(!callbacks.containsKey(id)) return false
        return callbacks.remove(id) != null
    }

    private fun runWithCallbacks(
        callback: (callback: IRecognitionCallback, metadata: RecognitionCallbackMetadata) -> Unit
    ) {
        with(callbacks.iterator()) {
            forEach {
                try {
                    callback(it.value.callback, it.value.metadata)
                }catch (e: RemoteException){
                    //Binder is dead, remove the callback
                    remove()
                }
            }
        }
    }

    /**
     *  Notifies all callbacks that recording is in progress. This is also required for ON_DEMAND
     *  callbacks, as those can use it to know that there's already an NNFP request in progress
     *  which will need to finish first.
     */
    fun onStartRecording() {
        state = State.RECORDING
        runWithCallbacks { callback, _ -> callback.onRecordingStarted() }
    }

    /**
     *  Notifies callbacks of the same source that recognition has started
     */
    fun onStartRecognizing(source: RecognitionSource) {
        state = State.RECOGNIZING
        lastMetadata = null
        runWithCallbacks { callback, metadata ->
            //Only notify the callback of the same type
            if(metadata.recognitionSource != source) return@runWithCallbacks
            callback.onRecognitionStarted()
        }
    }

    /**
     *  Notifies the required callbacks that recognition has been skipped. These are only sent
     *  to NNFP callbacks as on demand recognitions proceed regardless
     */
    fun onRecognitionSkipped(skippedReason: SkippedReason, source: RecognitionSource, force: Boolean = false) {
        state = State.IDLE
        runningSource = null
        val recognitionFailureReason = when(skippedReason){
            SkippedReason.SYSTEM_USER_NOT_IN_FOREGROUND ->
                RecognitionFailureReason.SkippedSystemUserNotInForeground
            SkippedReason.MUSIC ->
                RecognitionFailureReason.SkippedMusicPlaying
            SkippedReason.ON_CALL ->
                RecognitionFailureReason.SkippedOnCall
            SkippedReason.AUDIO_RECORD_FAILED ->
                RecognitionFailureReason.SkippedAudioRecordFailed
        }
        val recognitionFailure = RecognitionFailure(recognitionFailureReason, source, null)
        runWithCallbacks { callback, metadata ->
            if(metadata.recognitionSource == RecognitionSource.NNFP && !force){
                return@runWithCallbacks
            }
            callback.onRecognitionFailed(recognitionFailure)
        }
    }

    fun onMetadataCalculated(recognitionMetadata: RecognitionMetadata) {
        lastMetadata = recognitionMetadata
    }

    fun onRecognitionComplete(recognitionResult: RecognitionResult) {
        Thread {
            awaitMetadata()
            state = State.IDLE
            runningSource = null
            runWithCallbacks { callback, metadata ->
                callback.onRecognitionSucceeded(
                    recognitionResult.stripAudioIfNeeded(metadata.includeAudio),
                    lastMetadata
                )
            }
        }.start()
    }

    fun onRecognitionFailed(recognitionFailure: RecognitionFailure) {
        state = State.IDLE
        runningSource = null
        runWithCallbacks { callback, metadata ->
            if(recognitionFailure.source == RecognitionSource.NNFP
                && metadata.recognitionSource == RecognitionSource.ON_DEMAND) return@runWithCallbacks
            callback.onRecognitionFailed(recognitionFailure.stripAudioIfNeeded(metadata.includeAudio))
        }
    }

    fun getExpireTime(): Long? {
        return lastMetadata?.remainingTime
    }

    fun getRunningSource(): RecognitionSource? {
        return runningSource
    }

    fun setRunningSource(runningSource: RecognitionSource){
        this.runningSource = runningSource
    }

    private fun awaitMetadata() {
        val startTime = System.currentTimeMillis()
        while(lastMetadata == null){
            if(System.currentTimeMillis() - startTime >= METADATA_TIMEOUT) {
                break
            }
            Thread.sleep(10)
        }
    }

    private class Callback(
        val callback: IRecognitionCallback,
        val metadata: RecognitionCallbackMetadata
    )

    enum class State {
        IDLE, RECORDING, RECOGNIZING
    }

    enum class SkippedReason {
        MUSIC, ON_CALL, SYSTEM_USER_NOT_IN_FOREGROUND, AUDIO_RECORD_FAILED
    }

}