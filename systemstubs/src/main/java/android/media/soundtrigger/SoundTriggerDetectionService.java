/*
 * Copyright (C) 2018 The Android Open Source Project
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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.soundtrigger.SoundTrigger;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

public abstract class SoundTriggerDetectionService extends Service {

    private static final String LOG_TAG = SoundTriggerDetectionService.class.getSimpleName();
    private static final boolean DEBUG = false;
    private final Object mLock = new Object();

    @Override
    protected final void attachBaseContext(Context base) {
        throw new RuntimeException("Stub!");
    }

    /**
     * The system has connected to this service for the recognition registered for the model
     * {@code uuid}.
     *
     * <p> This is called before any operations are delivered.
     *
     * @param uuid   The {@code uuid} of the model the recognitions is registered for
     * @param params The {@code params} passed when the recognition was started
     */
    @MainThread
    public void onConnected(@NonNull UUID uuid, @Nullable Bundle params) {
        throw new RuntimeException("Stub!");
    }

    /**
     * The system has disconnected from this service for the recognition registered for the model
     * {@code uuid}.
     *
     * <p>Once this is called {@link #operationFinished} cannot be called anymore for
     * {@code uuid}.
     *
     * <p> {@link #onConnected(UUID, Bundle)} is called before any further operations are delivered.
     *
     * @param uuid   The {@code uuid} of the model the recognitions is registered for
     * @param params The {@code params} passed when the recognition was started
     */
    @MainThread
    public void onDisconnected(@NonNull UUID uuid, @Nullable Bundle params) {
        /* do nothing */
    }

    /**
     * A new generic sound trigger event has been detected.
     *
     * @param uuid   The {@code uuid} of the model the recognition is registered for
     * @param params The {@code params} passed when the recognition was started
     * @param opId The id of this operation. Once the operation is done, this service needs to call
     *             {@link #operationFinished(UUID, int)}
     * @param event The event that has been detected
     */
    @MainThread
    public void onGenericRecognitionEvent(@NonNull UUID uuid, @Nullable Bundle params, int opId,
                                              @NonNull SoundTrigger.RecognitionEvent event) {
        throw new RuntimeException("Stub!");
    }

    /**
     * A error has been detected.
     *
     * @param uuid   The {@code uuid} of the model the recognition is registered for
     * @param params The {@code params} passed when the recognition was started
     * @param opId The id of this operation. Once the operation is done, this service needs to call
     *             {@link #operationFinished(UUID, int)}
     * @param status The error code detected
     */
    @MainThread
    public void onError(@NonNull UUID uuid, @Nullable Bundle params, int opId, int status) {
        throw new RuntimeException("Stub!");
    }

    /**
     * An operation took too long and should be stopped.
     *
     * @param uuid   The {@code uuid} of the model the recognition is registered for
     * @param params The {@code params} passed when the recognition was started
     * @param opId The id of the operation that took too long
     */
    @MainThread
    public abstract void onStopOperation(@NonNull UUID uuid, @Nullable Bundle params, int opId);

    /**
     * Tell that the system that an operation has been fully processed.
     *
     * @param uuid The {@code uuid} of the model the recognition is registered for
     * @param opId The id of the operation that is processed
     */
    public final void operationFinished(@Nullable UUID uuid, int opId) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public final IBinder onBind(Intent intent) {
        throw new RuntimeException("Stub!");
    }

    @CallSuper
    @Override
    public boolean onUnbind(Intent intent) {
        throw new RuntimeException("Stub!");
    }

}