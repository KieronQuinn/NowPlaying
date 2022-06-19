package android.service.attention

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.NonNull

/**
 *  No-op version of [AttentionService] for Android < 10
 */
abstract class AttentionService: Service() {

    override fun onBind(intent: Intent): IBinder? {
        //No-op
        return null
    }

    /**
     * Checks the user attention and calls into the provided callback.
     *
     * @param callback the callback to return the result to
     */
    abstract fun onCheckAttention(@NonNull callback: AttentionCallback?)

    /**
     * Cancels pending work for a given callback.
     *
     * Implementation must call back with a failure code of [.ATTENTION_FAILURE_CANCELLED].
     */
    abstract fun onCancelAttentionCheck(@NonNull callback: AttentionCallback?)

    /** Callbacks for AttentionService results.  */
    class AttentionCallback {

        /**
         * Signals a success and provides the result code.
         *
         * @param timestamp of when the attention signal was computed; system throttles the requests
         * so this is useful to know how fresh the result is.
         */
        fun onSuccess(result: Int, timestamp: Long) {
            //No-op
        }

        /** Signals a failure and provides the error code.  */
        fun onFailure(error: Int) {
            //No-op
        }
    }

}