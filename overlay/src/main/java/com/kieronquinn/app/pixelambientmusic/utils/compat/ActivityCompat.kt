package com.kieronquinn.app.pixelambientmusic.utils.compat

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher

object ActivityCompat {

    @JvmStatic
    fun getOnBackInvokedDispatcher(item: Any): OnBackInvokedDispatcher {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when(item){
                is Activity -> item.onBackInvokedDispatcher
                is Dialog -> item.onBackInvokedDispatcher
                else -> throw RuntimeException("Invalid item type: ${item::class.java.simpleName}")
            }
        } else {
            //Return no-op version since dispatcher does not exist below T
            @SuppressLint("NewApi")
            object: OnBackInvokedDispatcher {
                override fun registerOnBackInvokedCallback(
                    priority: Int, callback: OnBackInvokedCallback
                ) {
                    //No-op
                }

                override fun unregisterOnBackInvokedCallback(callback: OnBackInvokedCallback) {
                    //No-op
                }
            }
        }
    }

}