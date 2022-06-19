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

import android.content.ComponentName;
import android.content.Context;
import android.hardware.soundtrigger.ModelParams;
import android.hardware.soundtrigger.SoundTrigger;
import android.hardware.soundtrigger.SoundTrigger.ModelParamRange;
import android.hardware.soundtrigger.SoundTrigger.RecognitionConfig;
import android.hardware.soundtrigger.SoundTrigger.SoundModel;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.internal.app.ISoundTriggerService;

import java.util.UUID;

/**
 * This class provides management of non-voice (general sound trigger) based sound recognition
 * models. Usage of this class is restricted to system or signature applications only. This allows
 * OEMs to write apps that can manage non-voice based sound trigger models.
 *
 * @hide
 */
public final class SoundTriggerManager {

    public SoundTriggerManager(Context context, ISoundTriggerService soundTriggerService) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Updates the given sound trigger model.
     */
    public void updateModel(Model model) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Get {@link Model} which is registered with the passed UUID
     *
     * @param soundModelId UUID associated with a loaded model
     * @return {@link Model} associated with UUID soundModelId
     */
    @Nullable
    public Model getModel(UUID soundModelId) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Deletes the sound model represented by the provided UUID.
     */
    public void deleteModel(UUID soundModelId) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Creates an instance of {@link SoundTriggerDetector} which can be used to start/stop
     * recognition on the model and register for triggers from the model. Note that this call
     * invalidates any previously returned instances for the same sound model Uuid.
     *
     * @param soundModelId UUID of the sound model to create the receiver object for.
     * @param callback Instance of the {@link SoundTriggerDetector#Callback} object for the
     * callbacks for the given sound model.
     * @param handler The Handler to use for the callback operations. A null value will use the
     * current thread's Looper.
     * @return Instance of {@link SoundTriggerDetector} or null on error.
     */
    @Nullable
    public SoundTriggerDetector createSoundTriggerDetector(UUID soundModelId,
                                                           @NonNull SoundTriggerDetector.Callback callback, @Nullable Handler handler) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Class captures the data and fields that represent a non-keyphrase sound model. Use the
     * factory constructor {@link Model#create()} to create an instance.
     */
    // We use encapsulation to expose the SoundTrigger.GenericSoundModel as a SystemApi. This
    // prevents us from exposing SoundTrigger.GenericSoundModel as an Api.
    public static class Model {

        /**
         * @hide
         */
        Model(SoundTrigger.GenericSoundModel soundTriggerModel) {
            throw new RuntimeException("Stub!");
        }

        /**
         * Factory constructor to a voice model to be used with {@link SoundTriggerManager}
         *
         * @param modelUuid Unique identifier associated with the model.
         * @param vendorUuid Unique identifier associated the calling vendor.
         * @param data Model's data.
         * @param version Version identifier for the model.
         * @return Voice model
         */
        @NonNull
        public static Model create(@NonNull UUID modelUuid, @NonNull UUID vendorUuid,
                                   @Nullable byte[] data, int version) {
            throw new RuntimeException("Stub!");
        }

        /**
         * Factory constructor to a voice model to be used with {@link SoundTriggerManager}
         *
         * @param modelUuid Unique identifier associated with the model.
         * @param vendorUuid Unique identifier associated the calling vendor.
         * @param data Model's data.
         * @return Voice model
         */
        @NonNull
        public static Model create(@NonNull UUID modelUuid, @NonNull UUID vendorUuid,
                                   @Nullable byte[] data) {
            throw new RuntimeException("Stub!");
        }

        /**
         * Get the model's unique identifier
         *
         * @return UUID associated with the model
         */
        @NonNull
        public UUID getModelUuid() {
            throw new RuntimeException("Stub!");
        }

        /**
         * Get the model's vendor identifier
         *
         * @return UUID associated with the vendor of the model
         */
        @NonNull
        public UUID getVendorUuid() {
            throw new RuntimeException("Stub!");
        }

        /**
         * Get the model's version
         *
         * @return Version associated with the model
         */
        public int getVersion() {
            throw new RuntimeException("Stub!");
        }

        /**
         * Get the underlying model data
         *
         * @return Backing data of the model
         */
        @Nullable
        public byte[] getModelData() {
            throw new RuntimeException("Stub!");
        }

        /**
         * @hide
         */
        SoundTrigger.GenericSoundModel getGenericSoundModel() {
            throw new RuntimeException("Stub!");
        }
    }


    /**
     * Default message type.
     */
    public static final int FLAG_MESSAGE_TYPE_UNKNOWN = -1;

    /**
     * Contents of EXTRA_MESSAGE_TYPE extra for a RecognitionEvent.
     */
    public static final int FLAG_MESSAGE_TYPE_RECOGNITION_EVENT = 0;

    /**
     * Contents of EXTRA_MESSAGE_TYPE extra for recognition error events.
     */
    public static final int FLAG_MESSAGE_TYPE_RECOGNITION_ERROR = 1;

    /**
     * Contents of EXTRA_MESSAGE_TYPE extra for a recognition paused events.
     */
    public static final int FLAG_MESSAGE_TYPE_RECOGNITION_PAUSED = 2;

    /**
     * Contents of EXTRA_MESSAGE_TYPE extra for recognition resumed events.
     */
    public static final int FLAG_MESSAGE_TYPE_RECOGNITION_RESUMED = 3;

    /**
     * Extra key in the intent for the type of the message.
     */
    public static final String EXTRA_MESSAGE_TYPE = "android.media.soundtrigger.MESSAGE_TYPE";

    /**
     * Extra key in the intent that holds the RecognitionEvent parcelable.
     */
    public static final String EXTRA_RECOGNITION_EVENT = "android.media.soundtrigger.RECOGNITION_EVENT";

    /**
     * Extra key in the intent that holds the status in an error message.
     */
    public static final String EXTRA_STATUS = "android.media.soundtrigger.STATUS";

    /**
     * Loads a given sound model into the sound trigger. Note the model will be unloaded if there is
     * an error/the system service is restarted.
     */
    public int loadSoundModel(SoundModel soundModel) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Starts recognition for the given model id. All events from the model will be sent to the
     * service.
     *
     * <p>This only supports generic sound trigger events. For keyphrase events, please use
     * {@link android.service.voice.VoiceInteractionService}.
     *
     * @param soundModelId Id of the sound model
     * @param params Opaque data sent to each service call of the service as the {@code params}
     *               argument
     * @param detectionService The component name of the service that should receive the events.
     *                         Needs to subclass {@link SoundTriggerDetectionService}
     * @param config Configures the recognition
     *
     * @return {@link SoundTrigger#STATUS_OK} if the recognition could be started, error code
     *         otherwise
     *
     */
    public int startRecognition(@NonNull UUID soundModelId, @Nullable Bundle params,
                                @NonNull ComponentName detectionService, @NonNull RecognitionConfig config) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Stops the given model's recognition.
     */
    public int stopRecognition(UUID soundModelId) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Removes the given model from memory. Will also stop any pending recognitions.
     */
    public int unloadSoundModel(UUID soundModelId) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Returns true if the given model has had detection started on it.
     */
    public boolean isRecognitionActive(UUID soundModelId) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Get the amount of time (in milliseconds) an operation of the
     * {@link ISoundTriggerDetectionService} is allowed to ask.
     *
     * @return The amount of time an sound trigger detection service operation is allowed to last
     */
    public int getDetectionServiceOperationsTimeout() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Asynchronously get state of the indicated model.  The model state is returned as
     * a recognition event in the callback that was registered in the startRecognition
     * method.
     */
    public int getModelState(UUID soundModelId) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Get the hardware sound trigger module properties currently loaded.
     *
     * @return The properties currently loaded. Returns null if no supported hardware loaded.
     */
    public SoundTrigger.ModuleProperties getModuleProperties() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Set a model specific {@link ModelParams} with the given value. This
     * parameter will keep its value for the duration the model is loaded regardless of starting and
     * stopping recognition. Once the model is unloaded, the value will be lost.
     * {@link SoundTriggerManager#queryParameter} should be checked first before calling this
     * method.
     *
     * @param soundModelId UUID of model to apply the parameter value to.
     * @param modelParam   {@link ModelParams}
     * @param value        Value to set
     * @return - {@link SoundTrigger#STATUS_OK} in case of success
     *         - {@link SoundTrigger#STATUS_NO_INIT} if the native service cannot be reached
     *         - {@link SoundTrigger#STATUS_BAD_VALUE} invalid input parameter
     *         - {@link SoundTrigger#STATUS_INVALID_OPERATION} if the call is out of sequence or
     *           if API is not supported by HAL
     */
    public int setParameter(@Nullable UUID soundModelId, int modelParam, int value) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Get a model specific {@link ModelParams}. This parameter will keep its value
     * for the duration the model is loaded regardless of starting and stopping recognition.
     * Once the model is unloaded, the value will be lost. If the value is not set, a default
     * value is returned. See {@link ModelParams} for parameter default values.
     * {@link SoundTriggerManager#queryParameter} should be checked first before
     * calling this method. Otherwise, an exception can be thrown.
     *
     * @param soundModelId UUID of model to get parameter
     * @param modelParam   {@link ModelParams}
     * @return value of parameter
     */
    public int getParameter(@NonNull UUID soundModelId, int modelParam) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Determine if parameter control is supported for the given model handle.
     * This method should be checked prior to calling {@link SoundTriggerManager#setParameter} or
     * {@link SoundTriggerManager#getParameter}.
     *
     * @param soundModelId handle of model to get parameter
     * @param modelParam {@link ModelParams}
     * @return supported range of parameter, null if not supported
     */
    @Nullable
    public ModelParamRange queryParameter(@Nullable UUID soundModelId, int modelParam) {
        throw new RuntimeException("Stub!");
    }
}