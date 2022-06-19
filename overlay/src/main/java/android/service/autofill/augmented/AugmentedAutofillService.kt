package android.service.autofill.augmented

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 *  No-op version of [AugmentedAutofillService] for Android < 10
 */
abstract class AugmentedAutofillService: Service() {

    override fun onBind(intent: Intent): IBinder? {
        //No-op
        return null
   }

    open fun onConnected() {
        //No-op
    }

    open fun onDisconnected() {
        //No-op
    }

}