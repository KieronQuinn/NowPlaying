package com.kieronquinn.app.pixelambientmusic.service

import android.app.Service
import android.content.*
import android.hardware.soundtrigger.SoundTrigger
import android.media.AudioFormat
import android.media.AudioManager
import android.media.soundtrigger.ISoundTriggerDetectionService
import android.os.IBinder
import android.os.ParcelUuid
import android.os.RemoteException
import com.google.intelligence.sense.ambientmusic.AmbientMusicDetector
import com.google.intelligence.sense.ondemand.InternalBroadcastReceiver
import com.kieronquinn.app.pixelambientmusic.IRecognitionCallback
import com.kieronquinn.app.pixelambientmusic.IRecognitionService
import com.kieronquinn.app.pixelambientmusic.components.albumart.AlbumArtRetriever
import com.kieronquinn.app.pixelambientmusic.components.recogniser.MusicRecogniser
import com.kieronquinn.app.pixelambientmusic.components.settings.SettingsStateHandler
import com.kieronquinn.app.pixelambientmusic.config.DeviceConfigOverrides
import com.kieronquinn.app.pixelambientmusic.model.*
import com.kieronquinn.app.pixelambientmusic.providers.SettingsProvider
import java.util.*
import java.util.concurrent.Executors

/**
 *  Entry point service for Ambient Music Mod. Directs calls to non-exported broadcast receivers
 *  and services, as well as managing remote settings
 */
class RecognitionService: Service(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private const val SERVICE_CONNECT_TIMEOUT = 2500L
        private val MODEL_UUID = ParcelUuid(UUID.fromString("9f6ad62a-1f0b-11e7-87c5-40a8f03d3f15"))
        private const val SUPERPACKS_SERVICE =
            "com.google.android.apps.miphone.aiai.common.superpacks.impl.AiAiPersistentDownloadJobService"
    }

    private val audioManager by lazy {
        getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private val service = RecognitionServiceImpl()
    private val serviceConnectExecutor = Executors.newSingleThreadExecutor()

    private var detectionService: ISoundTriggerDetectionService? = null
    private var detectionServiceConnection: ServiceConnection? = null

    private val onDemandIntent by lazy {
        Intent("com.google.intelligence.sense.ambientmusic.ondemand.AOD_CLICK").apply {
            component = ComponentName(this@RecognitionService, InternalBroadcastReceiver::class.java)
        }
    }

    private val musicDetectionServiceIntent by lazy {
        Intent(this, AmbientMusicDetector.Service::class.java)
    }

    override fun onBind(intent: Intent): IBinder {
        return service
    }

    @Synchronized
    private fun runWithDetectionService(block: (ISoundTriggerDetectionService) -> Unit) {
        detectionService?.let {
            try {
                return block(it)
            }catch (e: RemoteException){
                //Probably a dead connection, try again once
                return@let
            }
        }
        val serviceConnection = object: ServiceConnection {
            override fun onServiceConnected(component: ComponentName, binder: IBinder) {
                val service = ISoundTriggerDetectionService.Stub.asInterface(binder)
                detectionService = service
                detectionServiceConnection = this
            }

            override fun onServiceDisconnected(component: ComponentName) {
                detectionService = null
                detectionServiceConnection = null
            }
        }
        bindService(
            musicDetectionServiceIntent,
            Context.BIND_AUTO_CREATE,
            serviceConnectExecutor,
            serviceConnection
        )
        val startTime = System.currentTimeMillis()
        while(detectionService == null){
            if(System.currentTimeMillis() - startTime > SERVICE_CONNECT_TIMEOUT){
                throw RuntimeException("Timeout: Failed to connect to service")
            }
            Thread.sleep(10)
        }
        block(detectionService!!)
    }

    private fun createGenericRecognitionEvent(): SoundTrigger.GenericRecognitionEvent {
        val audioSessionId = audioManager.generateAudioSessionId()
        val audioFormat = AudioFormat.Builder().apply {
            setSampleRate(16000)
            setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            setChannelMask(AudioFormat.CHANNEL_IN_MONO)
        }.build()
        return SoundTrigger.GenericRecognitionEvent(
            0,
            0,
            true,
            audioSessionId,
            0,
            0,
            false,
            audioFormat,
            ByteArray(0)
        )
    }

    override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences?, key: String) {
        if(SettingsStateHandler.isImportantPreference(key)){
            SettingsProvider.notifyUpdate(this)
        }
    }

    override fun onCreate() {
        super.onCreate()
        SettingsStateHandler.registerListener(this, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        SettingsStateHandler.unregisterListener(this, this)
    }

    private inner class RecognitionServiceImpl: IRecognitionService.Stub() {

        override fun ping(): Boolean {
            return true
        }

        /**
         *  Adds a [IRecognitionCallback] [callback] to receive events from a recognition. This
         *  does **not** start a recognition (call [requestRecognition] for that), but it will
         *  receive events if a recognition is already running - including before
         *  [requestRecognition] is called.
         *
         *  Returns the id of the callback, pass to [removeRecognitionCallback] to remove it.
         */
        override fun addRecognitionCallback(
            callback: IRecognitionCallback,
            callbackMetadata: RecognitionCallbackMetadata
        ): String {
            return MusicRecogniser.addCallback(callback, callbackMetadata)
        }

        /**
         *  Remove a given callback, returns if it existed and was removed.
         *
         *  [id] is the ID returned from [addRecognitionCallback]
         */
        override fun removeRecognitionCallback(id: String): Boolean {
            return MusicRecogniser.removeCallback(id)
        }

        /**
         *  Request an on-device music recognition. If **any** recognition is already running,
         *  this will not start a new one, including on-demand recognitions, since on-demand
         *  takes priority.
         */
        @Synchronized
        override fun requestRecognition() {
            if(checkIfRunning(RecognitionSource.NNFP)) return
            MusicRecogniser.setRunningSource(RecognitionSource.NNFP)
            runWithDetectionService {
                if(checkIfRunning(RecognitionSource.NNFP)) return@runWithDetectionService
                it.onGenericRecognitionEvent(
                    MODEL_UUID,
                    0,
                    createGenericRecognitionEvent()
                )
            }
        }

        /**
         *  Request an on-demand (online) music recognition. If an on-device recognition is already
         *  running, this will take priority (but the on-demand callback will not be called for
         *  an NNFP result).
         */
        override fun requestOnDemandRecognition() {
            if(checkIfRunning(RecognitionSource.ON_DEMAND)) return
            MusicRecogniser.setRunningSource(RecognitionSource.ON_DEMAND)
            sendBroadcast(onDemandIntent)
        }

        private fun checkIfRunning(requestedSource: RecognitionSource): Boolean {
            MusicRecogniser.getRunningSource()?.let { runningSource ->
                //If NNFP is running and ON_DEMAND is requested, it will get stuck, so report busy
                if(requestedSource == RecognitionSource.ON_DEMAND && runningSource == RecognitionSource.NNFP) {
                    MusicRecogniser.onRecognitionFailed(RecognitionFailure(
                        RecognitionFailureReason.Busy(runningSource), requestedSource, null
                    ))
                    return true
                }
            }
            return false
        }

        override fun onConfigChanged(configNames: MutableList<String>) {
            DeviceConfigOverrides.notifyChanges(this@RecognitionService, configNames)
        }

        override fun updateSettingsState(change: SettingsStateChange) {
            SettingsStateHandler.saveSettingsStateChange(this@RecognitionService, change)
        }

        override fun clearAlbumArtCache() {
            AlbumArtRetriever.INSTANCE.clearCache()
        }

    }

}