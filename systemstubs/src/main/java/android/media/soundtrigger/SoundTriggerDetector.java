/**
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.media.soundtrigger;

import android.hardware.soundtrigger.SoundTrigger.ModuleProperties;
import android.media.AudioFormat;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.internal.app.ISoundTriggerSession;

import java.io.PrintWriter;
import java.util.UUID;

/**
 * A class that allows interaction with the actual sound trigger detection on the system.
 * Sound trigger detection refers to a detectors that match generic sound patterns that are
 * not voice-based. The voice-based recognition models should utilize the {@link
 * VoiceInteractionService} instead. Access to this class is protected by a permission
 * granted only to system or privileged apps.
 */
public final class SoundTriggerDetector {

    /**
     * Empty flag for {@link #startRecognition(int)}.
     */
    public static final int RECOGNITION_FLAG_NONE = 0;

    /**
     * Recognition flag for {@link #startRecognition(int)} that indicates
     * whether the trigger audio for hotword needs to be captured.
     */
    public static final int RECOGNITION_FLAG_CAPTURE_TRIGGER_AUDIO = 0x1;

    /**
     * Recognition flag for {@link #startRecognition(int)} that indicates
     * whether the recognition should keep going on even after the
     * model triggers.
     * If this flag is specified, it's possible to get multiple
     * triggers after a call to {@link #startRecognition(int)}, if the model
     * triggers multiple times.
     * When this isn't specified, the default behavior is to stop recognition once the
     * trigger happens, till the caller starts recognition again.
     */
    public static final int RECOGNITION_FLAG_ALLOW_MULTIPLE_TRIGGERS = 0x2;

    /**
     * Audio capabilities flag for {@link #startRecognition(int)} that indicates
     * if the underlying recognition should use AEC.
     * This capability may or may not be supported by the system, and support can be queried
     * by calling {@link SoundTriggerManager#getModuleProperties()} and checking
     * {@link ModuleProperties#audioCapabilities}. The corresponding capabilities field for
     * this flag is {@link ModuleProperties#AUDIO_CAPABILITY_ECHO_CANCELLATION}.
     * If this flag is passed without the audio capability supported, there will be no audio effect
     * applied.
     */
    public static final int RECOGNITION_FLAG_ENABLE_AUDIO_ECHO_CANCELLATION = 0x4;

    /**
     * Audio capabilities flag for {@link #startRecognition(int)} that indicates
     * if the underlying recognition should use noise suppression.
     * This capability may or may not be supported by the system, and support can be queried
     * by calling {@link SoundTriggerManager#getModuleProperties()} and checking
     * {@link ModuleProperties#audioCapabilities}. The corresponding capabilities field for
     * this flag is {@link ModuleProperties#AUDIO_CAPABILITY_NOISE_SUPPRESSION}.
     * If this flag is passed without the audio capability supported, there will be no audio effect
     * applied.
     */
    public static final int RECOGNITION_FLAG_ENABLE_AUDIO_NOISE_SUPPRESSION = 0x8;

    /**
     * Recognition flag for {@link #startRecognition(int)} that indicates whether the recognition
     * should continue after battery saver mode is enabled.
     * When this flag is specified, the caller will be checked for
     * {@link android.Manifest.permission#SOUND_TRIGGER_RUN_IN_BATTERY_SAVER} permission granted.
     */
    public static final int RECOGNITION_FLAG_RUN_IN_BATTERY_SAVER = 0x10;

    /**
     * Additional payload for {@link Callback#onDetected}.
     */
    public static class EventPayload {

        private EventPayload(boolean triggerAvailable, boolean captureAvailable,
                AudioFormat audioFormat, int captureSession, byte[] data) {
            throw new RuntimeException("Stub!");
        }

        /**
         * Gets the format of the audio obtained using {@link #getTriggerAudio()}.
         * May be null if there's no audio present.
         */
        @Nullable
        public AudioFormat getCaptureAudioFormat() {
            throw new RuntimeException("Stub!");
        }

        /**
         * Gets the raw audio that triggered the detector.
         * This may be null if the trigger audio isn't available.
         * If non-null, the format of the audio can be obtained by calling
         * {@link #getCaptureAudioFormat()}.
         *
         * @see AlwaysOnHotwordDetector#RECOGNITION_FLAG_CAPTURE_TRIGGER_AUDIO
         */
        @Nullable
        public byte[] getTriggerAudio() {
            throw new RuntimeException("Stub!");
        }

        /**
         * Gets the opaque data passed from the detection engine for the event.
         * This may be null if it was not populated by the engine, or if the data is known to
         * contain the trigger audio.
         *
         * @see #getTriggerAudio
         */
        @Nullable
        public byte[] getData() {
            throw new RuntimeException("Stub!");
        }

        /**
         * Gets the session ID to start a capture from the DSP.
         * This may be null if streaming capture isn't possible.
         * If non-null, the format of the audio that can be captured can be
         * obtained using {@link #getCaptureAudioFormat()}.
         *
         * TODO: Candidate for Public API when the API to start capture with a session ID
         * is made public.
         *
         * TODO: Add this to {@link #getCaptureAudioFormat()}:
         * "Gets the format of the audio obtained using {@link #getTriggerAudio()}
         * or {@link #getCaptureSession()}. May be null if no audio can be obtained
         * for either the trigger or a streaming session."
         *
         * TODO: Should this return a known invalid value instead?
         */
        @Nullable
        public Integer getCaptureSession() {
            throw new RuntimeException("Stub!");
        }
    }

    public static abstract class Callback {
        /**
         * Called when the availability of the sound model changes.
         */
        public abstract void onAvailabilityChanged(int status);

        /**
         * Called when the sound model has triggered (such as when it matched a
         * given sound pattern).
         */
        public abstract void onDetected(@NonNull EventPayload eventPayload);

        /**
         *  Called when the detection fails due to an error.
         */
        public abstract void onError();

        /**
         * Called when the recognition is paused temporarily for some reason.
         * This is an informational callback, and the clients shouldn't be doing anything here
         * except showing an indication on their UI if they have to.
         */
        public abstract void onRecognitionPaused();

        /**
         * Called when the recognition is resumed after it was temporarily paused.
         * This is an informational callback, and the clients shouldn't be doing anything here
         * except showing an indication on their UI if they have to.
         */
        public abstract void onRecognitionResumed();
    }

    /**
     * This class should be constructed by the {@link SoundTriggerManager}.
     */
    SoundTriggerDetector(ISoundTriggerSession soundTriggerSession, UUID soundModelId,
            @NonNull Callback callback, @Nullable Handler handler) {throw new RuntimeException("Stub!");
    }

    /**
     * Starts recognition on the associated sound model. Result is indicated via the
     * {@link Callback}.
     * @return Indicates whether the call succeeded or not.
     */
    public boolean startRecognition(int recognitionFlags) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Stops recognition for the associated model.
     */
    public boolean stopRecognition() {
        throw new RuntimeException("Stub!");
    }

    /**
     * @hide
     */
    public void dump(String prefix, PrintWriter pw) {
        throw new RuntimeException("Stub!");
    }
}