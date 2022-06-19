package com.kieronquinn.app.pixelambientmusic.utils

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.kieronquinn.app.ambientmusicmod.IShellProxy
import com.kieronquinn.app.pixelambientmusic.components.recogniser.MusicRecogniser
import com.kieronquinn.app.pixelambientmusic.config.DeviceConfigOverrides
import com.kieronquinn.app.pixelambientmusic.model.RecognitionSource
import com.kieronquinn.app.pixelambientmusic.service.ServiceController
import com.kieronquinn.app.pixelambientmusic.utils.extensions.applyGain
import com.kieronquinn.app.pixelambientmusic.utils.extensions.toShortArray

/**
 *  [AudioRecord] that intercepts all relevant calls and sends them to a remote AudioRecord within
 *  the Shizuku service (via Pixel Ambient Music). This allows for accessing the assistant mic.
 *
 *  Note that there is also a regular AudioRecord created under the proxy, but it is never used
 *  and is simply released at the same time the proxy one is. The "unused" constructor is also
 *  used via reflection elsewhere in the app, redirected from using the hidden constructor of
 *  regular AudioRecord using hooks.
 */
@SuppressLint("MissingPermission")
class ProxyAudioRecord(
    audioSource: Int,
    sampleRateInHz: Int,
    channelConfig: Int,
    audioFormat: Int,
    bufferSizeInBytes: Int
) : AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes) {

    companion object {
        private const val TAG = "ProxyAudioRecord"
        private const val DEVICE_CONFIG_KEY_GAIN = "NowPlaying__recording_gain"
    }

    constructor(
        attributes: AudioAttributes,
        audioFormat: AudioFormat,
        bufferSizeInBytes: Int,
        sessionId: Int
    ) : this(
        MediaRecorder.AudioSource.MIC,
        16000,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        bufferSizeInBytes
    ) {
        runWithService {
            it.AudioRecord_create(attributes, audioFormat, sessionId, bufferSizeInBytes)
        }
    }

    override fun startRecording() {
        MusicRecogniser.onStartRecording()
        runWithService {
            it.AudioRecord_startRecording()
        }
    }

    override fun release() {
        runWithService {
            it.AudioRecord_release()
        }
        //Release the dummy super-created AudioRecord too
        super.release()
    }

    override fun read(audioData: ShortArray, offsetInShorts: Int, sizeInShorts: Int): Int {
        //Prep for recognition - make sure libsense is loaded
        System.loadLibrary("sense")
        val byteData = ByteArray(sizeInShorts * 2)
        return runWithService {
            it.AudioRecord_read(byteData, offsetInShorts, sizeInShorts).also {
                byteData.toShortArray().applyGainIfRequired().copyInto(audioData)
            }
        }.also {
            //Check if the record failed and thus the app won't give us a result, so trigger a skip
            val bufferSize = bufferSizeInFrames
            val willFail = it < bufferSize
            if(willFail) reportAudioRecordFailed()
        }
    }

    override fun getBufferSizeInFrames(): Int {
        return runWithService {
            it.AudioRecord_getBufferSizeInFrames()
        }
    }

    override fun getFormat(): AudioFormat {
        return runWithService {
            it.AudioRecord_getFormat()
        }
    }

    override fun getSampleRate(): Int {
        return runWithService {
            it.AudioRecord_getSampleRate()
        }
    }

    /**
     *  Runs a [block] with the service ([IShellProxy]), catching exceptions and reporting them
     *  back to the client, which can delay and run again later when the service has restarted.
     */
    private fun <T> runWithService(block: (IShellProxy) -> T): T {
        return try {
            ServiceController.runWithService {
                block(it)
            }
        }catch (e: Exception){
            reportAudioRecordFailed()
            Log.e(TAG, "Error", e)
            throw e
        }
    }

    private fun reportAudioRecordFailed() {
        MusicRecogniser.onRecognitionSkipped(
            MusicRecogniser.SkippedReason.AUDIO_RECORD_FAILED,
            RecognitionSource.NNFP,
            true
        )
    }

    private fun ShortArray.applyGainIfRequired(): ShortArray {
        val gain = DeviceConfigOverrides.getValue(DEVICE_CONFIG_KEY_GAIN)?.toFloatOrNull()
        if(gain == null || gain == 1.0f) return this
        return applyGain(gain)
    }

}