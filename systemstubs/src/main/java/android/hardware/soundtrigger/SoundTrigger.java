package android.hardware.soundtrigger;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

public class SoundTrigger {

    /**
     * A RecognitionEvent is provided by the
     * {@code StatusListener#onRecognition(RecognitionEvent)}
     * callback upon recognition success or failure.
     */
    public static class RecognitionEvent {
        /**
         * Recognition status e.g RECOGNITION_STATUS_SUCCESS
         */
        public final int status;

        /**
         * Sound Model corresponding to this event callback
         */
        public final int soundModelHandle;

        /**
         * True if it is possible to capture audio from this utterance buffered by the hardware
         */
        public final boolean captureAvailable;

        /**
         * Audio session ID to be used when capturing the utterance with an AudioRecord
         * if captureAvailable() is true.
         */
        public final int captureSession;

        /**
         * Delay in ms between end of model detection and start of audio available for capture.
         * A negative value is possible (e.g. if keyphrase is also available for capture)
         */
        public final int captureDelayMs;

        /**
         * Duration in ms of audio captured before the start of the trigger. 0 if none.
         */

        public final int capturePreambleMs;

        /**
         * True if  the trigger (key phrase capture is present in binary data
         */
        public final boolean triggerInData;

        /**
         * Audio format of either the trigger in event data or to use for capture of the
         * rest of the utterance
         */
        @NonNull
        public final AudioFormat captureFormat;

        /**
         * Opaque data for use by system applications who know about voice engine internals,
         * typically during enrollment.
         */
        @NonNull
        public final byte[] data;

        public RecognitionEvent(int status, int soundModelHandle, boolean captureAvailable,
                                int captureSession, int captureDelayMs, int capturePreambleMs,
                                boolean triggerInData, @NonNull AudioFormat captureFormat, @Nullable byte[] data) {
            throw new RuntimeException("Stub!");
        }

        /**
         * Check if is possible to capture audio from this utterance buffered by the hardware.
         *
         * @return {@code true} iff a capturing is possible
         */
        public boolean isCaptureAvailable() {
            throw new RuntimeException("Stub!");
        }

        /**
         * Get the audio format of either the trigger in event data or to use for capture of the
         * rest of the utterance
         *
         * @return the audio format
         */
        @Nullable
        public AudioFormat getCaptureFormat() {
            throw new RuntimeException("Stub!");
        }

        /**
         * Get Audio session ID to be used when capturing the utterance with an {@link AudioRecord}
         * if {@link #isCaptureAvailable()} is true.
         *
         * @return The id of the capture session
         */
        public int getCaptureSession() {
            throw new RuntimeException("Stub!");
        }

        /**
         * Get the opaque data for use by system applications who know about voice engine
         * internals, typically during enrollment.
         *
         * @return The data of the event
         */
        @SuppressLint("MissingNullability")
        public byte[] getData() {
            throw new RuntimeException("Stub!");
        }

        public static final @NonNull Parcelable.Creator<RecognitionEvent> CREATOR
                = new Parcelable.Creator<RecognitionEvent>() {
            public RecognitionEvent createFromParcel(Parcel in) {
                return RecognitionEvent.fromParcel(in);
            }

            public RecognitionEvent[] newArray(int size) {
                return new RecognitionEvent[size];
            }
        };

        protected static RecognitionEvent fromParcel(Parcel in) {
            throw new RuntimeException("Stub!");
        }

        public int describeContents() {
            throw new RuntimeException("Stub!");
        }

        public void writeToParcel(Parcel dest, int flags) {
            throw new RuntimeException("Stub!");
        }

        @Override
        public int hashCode() {
            throw new RuntimeException("Stub!");
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            throw new RuntimeException("Stub!");
        }

        @NonNull
        @Override
        public String toString() {
            throw new RuntimeException("Stub!");
        }
    }

    /**
     * A SoundModel describes the attributes and contains the binary data used by the hardware
     * implementation to detect a particular sound pattern.
     * A specialized version {@link KeyphraseSoundModel} is defined for key phrase
     * sound models.
     */
    public static class SoundModel {

        public @interface SoundModelType {}

        /**
         * Undefined sound model type
         * @hide
         */
        public static final int TYPE_UNKNOWN = -1;

        /** Keyphrase sound model */
        public static final int TYPE_KEYPHRASE = 0;

        /**
         * A generic sound model. Use this type only for non-keyphrase sound models such as
         * ones that match a particular sound pattern.
         */
        public static final int TYPE_GENERIC_SOUND = 1;

        public SoundModel(@NonNull UUID uuid, @Nullable UUID vendorUuid, @SoundModelType int type,
                          @Nullable byte[] data, int version) {
            throw new RuntimeException("Stub!");
        }

        /** Unique sound model identifier */
        @NonNull
        public UUID getUuid() {
            throw new RuntimeException("Stub!");
        }

        /** Sound model type (e.g. TYPE_KEYPHRASE); */
        @SoundModelType
        public int getType() {
            throw new RuntimeException("Stub!");
        }

        /** Unique sound model vendor identifier */
        @NonNull
        public UUID getVendorUuid() {
            throw new RuntimeException("Stub!");
        }

        /** vendor specific version number of the model */
        public int getVersion() {
            throw new RuntimeException("Stub!");
        }

        /** Opaque data. For use by vendor implementation and enrollment application */
        @NonNull
        public byte[] getData() {
            throw new RuntimeException("Stub!");
        }

        @Override
        public int hashCode() {
            throw new RuntimeException("Stub!");
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            throw new RuntimeException("Stub!");
        }
    }

    /*****************************************************************************
     * A GenericSoundModel is a specialized {@link SoundModel} for non-voice sound
     * patterns.
     *
     * @hide
     ****************************************************************************/
    public static class GenericSoundModel extends SoundModel implements Parcelable {

        public static final @NonNull Creator<GenericSoundModel> CREATOR
                = new Creator<GenericSoundModel>() {
            public GenericSoundModel createFromParcel(Parcel in) {
                return GenericSoundModel.fromParcel(in);
            }

            public GenericSoundModel[] newArray(int size) {
                return new GenericSoundModel[size];
            }
        };

        public GenericSoundModel(@NonNull UUID uuid, @NonNull UUID vendorUuid,
                                 @Nullable byte[] data, int version) {
            super(uuid, vendorUuid, TYPE_GENERIC_SOUND, data, version);
            throw new RuntimeException("Stub!");
        }

        public GenericSoundModel(@NonNull UUID uuid, @NonNull UUID vendorUuid,
                                 @Nullable byte[] data) {
            this(uuid, vendorUuid, data, -1);
            throw new RuntimeException("Stub!");
        }

        @Override
        public int describeContents() {
            return 0;
        }

        private static GenericSoundModel fromParcel(Parcel in) {
            throw new RuntimeException("Stub!");
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            throw new RuntimeException("Stub!");
        }

        @Override
        public String toString() {
            throw new RuntimeException("Stub!");
        }
    }

    /**
     * A ModelParamRange is a representation of supported parameter range for a
     * given loaded model.
     */
    public static final class ModelParamRange implements Parcelable {

        public ModelParamRange(int start, int end) {
            throw new RuntimeException("Stub!");
        }

        private ModelParamRange(@NonNull Parcel in) {
            throw new RuntimeException("Stub!");
        }

        /**
         * Get the beginning of the param range
         *
         * @return The inclusive start of the supported range.
         */
        public int getStart() {
            throw new RuntimeException("Stub!");
        }

        /**
         * Get the end of the param range
         *
         * @return The inclusive end of the supported range.
         */
        public int getEnd() {
            throw new RuntimeException("Stub!");
        }

        @NonNull
        public static final Creator<ModelParamRange> CREATOR =
                new Creator<ModelParamRange>() {
                    @Override
                    @NonNull
                    public ModelParamRange createFromParcel(@NonNull Parcel in) {
                        return new ModelParamRange(in);
                    }

                    @Override
                    @NonNull
                    public ModelParamRange[] newArray(int size) {
                        return new ModelParamRange[size];
                    }
                };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public int hashCode() {
            throw new RuntimeException("Stub!");
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            throw new RuntimeException("Stub!");
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            throw new RuntimeException("Stub!");
        }

        @Override
        @NonNull
        public String toString() {
            throw new RuntimeException("Stub!");
        }
    }

    /**
     *  A RecognitionConfig is provided to
     *  {@link SoundTriggerModule#startRecognition(int, RecognitionConfig)} to configure the
     *  recognition request.
     */
    public static class RecognitionConfig implements Parcelable {


        protected RecognitionConfig(Parcel in) {
        }

        public static final Creator<RecognitionConfig> CREATOR = new Creator<RecognitionConfig>() {
            @Override
            public RecognitionConfig createFromParcel(Parcel in) {
                return new RecognitionConfig(in);
            }

            @Override
            public RecognitionConfig[] newArray(int size) {
                return new RecognitionConfig[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            throw new RuntimeException("Stub!");
        }
    }

    /*****************************************************************************
     * A ModuleProperties describes a given sound trigger hardware module
     * managed by the native sound trigger service. Each module has a unique
     * ID used to target any API call to this paricular module. Module
     * properties are returned by listModules() method.
     *
     ****************************************************************************/
    public static final class ModuleProperties implements Parcelable {

        /**
         * If set the underlying module supports AEC.
         * Describes bit field {@link ModuleProperties#mAudioCapabilities}
         */
        public static final int AUDIO_CAPABILITY_ECHO_CANCELLATION = 0x1;
        /**
         * If set, the underlying module supports noise suppression.
         * Describes bit field {@link ModuleProperties#mAudioCapabilities}
         */
        public static final int AUDIO_CAPABILITY_NOISE_SUPPRESSION = 0x2;

        public ModuleProperties(int id, @NonNull String implementor, @NonNull String description,
                                @NonNull String uuid, int version, @NonNull String supportedModelArch,
                                int maxSoundModels, int maxKeyphrases, int maxUsers,
                                int recognitionModes, boolean supportsCaptureTransition,
                                int maxBufferMs, boolean supportsConcurrentCapture, int powerConsumptionMw,
                                boolean returnsTriggerInEvent, int audioCapabilities) {
            throw new RuntimeException("Stub!");
        }

        /** Unique module ID provided by the native service */
        public int getId() {
            throw new RuntimeException("Stub!");
        }

        /** human readable voice detection engine implementor */
        @NonNull
        public String getImplementor() {
            throw new RuntimeException("Stub!");
        }

        /** human readable voice detection engine description */
        @NonNull
        public String getDescription() {
            throw new RuntimeException("Stub!");
        }

        /** Unique voice engine Id (changes with each version) */
        @NonNull
        public UUID getUuid() {
            throw new RuntimeException("Stub!");
        }

        /** Voice detection engine version */
        public int getVersion() {
            throw new RuntimeException("Stub!");
        }

        /**
         * String naming the architecture used for running the supported models.
         * (eg. a platform running models on a DSP could implement this string to convey the DSP
         * architecture used)
         */
        @NonNull
        public String getSupportedModelArch() {
            throw new RuntimeException("Stub!");
        }

        /** Maximum number of active sound models */
        public int getMaxSoundModels() {
            throw new RuntimeException("Stub!");
        }

        /** Maximum number of key phrases */
        public int getMaxKeyphrases() {
            throw new RuntimeException("Stub!");
        }

        /** Maximum number of users per key phrase */
        public int getMaxUsers() {
            throw new RuntimeException("Stub!");
        }

        /** Supported recognition modes (bit field, RECOGNITION_MODE_VOICE_TRIGGER ...) */
        public int getRecognitionModes() {
            throw new RuntimeException("Stub!");
        }

        /** Supports seamless transition to capture mode after recognition */
        public boolean isCaptureTransitionSupported() {
            throw new RuntimeException("Stub!");
        }

        /** Maximum buffering capacity in ms if supportsCaptureTransition() is true */
        public int getMaxBufferMillis() {
            throw new RuntimeException("Stub!");
        }

        /** Supports capture by other use cases while detection is active */
        public boolean isConcurrentCaptureSupported() {
            throw new RuntimeException("Stub!");
        }

        /** Rated power consumption when detection is active with TDB silence/sound/speech ratio */
        public int getPowerConsumptionMw() {
            throw new RuntimeException("Stub!");
        }

        /** Returns the trigger (key phrase) capture in the binary data of the
         * recognition callback event */
        public boolean isTriggerReturnedInEvent() {
            throw new RuntimeException("Stub!");
        }

        /**
         * Bit field encoding of the AudioCapabilities
         * supported by the firmware.
         */
        public int getAudioCapabilities() {
            throw new RuntimeException("Stub!");
        }

        public static final @NonNull Creator<ModuleProperties> CREATOR
                = new Creator<ModuleProperties>() {
            public ModuleProperties createFromParcel(Parcel in) {
                return ModuleProperties.fromParcel(in);
            }

            public ModuleProperties[] newArray(int size) {
                return new ModuleProperties[size];
            }
        };

        private static ModuleProperties fromParcel(Parcel in) {
            throw new RuntimeException("Stub!");
        }

        @Override
        public void writeToParcel(@SuppressLint("MissingNullability") Parcel dest, int flags) {
            throw new RuntimeException("Stub!");
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            throw new RuntimeException("Stub!");
        }

        @Override
        public int hashCode() {
            throw new RuntimeException("Stub!");
        }

        @Override
        public String toString() {
            throw new RuntimeException("Stub!");
        }
    }

    /**
     * Sub-class of RecognitionEvent specifically for sound-trigger based sound
     * models(non-keyphrase). Currently does not contain any additional fields.
     */
    public static class GenericRecognitionEvent extends RecognitionEvent implements Parcelable {

        public GenericRecognitionEvent(int status, int soundModelHandle,
                                       boolean captureAvailable, int captureSession, int captureDelayMs,
                                       int capturePreambleMs, boolean triggerInData, @NonNull AudioFormat captureFormat,
                                       @Nullable byte[] data) {
            super(status, soundModelHandle, captureAvailable, captureSession,
                    captureDelayMs, capturePreambleMs, triggerInData, captureFormat,
                    data);
            throw new RuntimeException("Stub!");
        }

        //Android 14+
        public GenericRecognitionEvent(int status, int soundModelHandle, boolean captureAvailable,
                                       int captureSession, int captureDelayMs,
                                       int capturePreambleMs, boolean triggerInData,
                                       AudioFormat captureFormat,
                                       byte[] data, long halEventReceivedMillis) {
            super(status, soundModelHandle, captureAvailable, captureSession,
                    captureDelayMs, capturePreambleMs, triggerInData, captureFormat,
                    data);
            throw new RuntimeException("Stub!");
        }

        public static final @NonNull Creator<GenericRecognitionEvent> CREATOR
                = new Creator<GenericRecognitionEvent>() {
            public GenericRecognitionEvent createFromParcel(Parcel in) {
                return GenericRecognitionEvent.fromParcelForGeneric(in);
            }

            public GenericRecognitionEvent[] newArray(int size) {
                return new GenericRecognitionEvent[size];
            }
        };

        private static GenericRecognitionEvent fromParcelForGeneric(Parcel in) {
            throw new RuntimeException("Stub!");
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            throw new RuntimeException("Stub!");
        }

        @Override
        public String toString() {
            throw new RuntimeException("Stub!");
        }
    }

}
