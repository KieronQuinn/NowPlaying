package android.provider

import java.util.concurrent.Executor

/**
 *  No-op version of [DeviceConfig] for Android < 10
 */
class DeviceConfig {

    fun getProperty(namespace: String, name: String): String? {
        return null
    }

    fun addOnPropertiesChangedListener(
        namespace: String,
        executor: Executor,
        onPropertiesChangedListener: OnPropertiesChangedListener
    ) {
        //No-op
    }

    interface OnPropertiesChangedListener {
        /**
         * Called when one or more properties have changed, providing a Properties object with all
         * of the changed properties. This object will contain only properties which have changed,
         * not the complete set of all properties belonging to the namespace.
         *
         * @param properties Contains the complete collection of properties which have changed for a
         * single namespace. This includes only those which were added, updated,
         * or deleted.
         */
        fun onPropertiesChanged(properties: Properties)
    }

    data class Properties(val namespace: String, val keyValueMap: Map<String, String>?)

}