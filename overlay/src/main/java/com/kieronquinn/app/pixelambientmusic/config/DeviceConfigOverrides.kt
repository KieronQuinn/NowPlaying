package com.kieronquinn.app.pixelambientmusic.config

import android.content.Context
import android.net.Uri
import android.provider.DeviceConfig
import com.kieronquinn.app.pixelambientmusic.IRecognitionService
import com.kieronquinn.app.pixelambientmusic.config.DeviceConfigOverrides.notifyChanges
import com.kieronquinn.app.pixelambientmusic.utils.extensions.map

/**
 *  Loads device config overrides from the Ambient Music Mod host, to replace calls to
 *  [DeviceConfig] with. A provider is used for the remote app to handle settings and storing
 *  of values.
 *
 *  The remote app can trigger a reload without restarting this app using the
 *  [IRecognitionService.onConfigChanged] method, which will call [notifyChanges].
 */
object DeviceConfigOverrides {

    private const val NAMESPACE = "device_personalization_services"

    private val listeners = ArrayList<DeviceConfig.OnPropertiesChangedListener>()

    private var FLAG_VALUES = emptyMap<String, String>()

    fun populateValues(context: Context) {
        val providerUri = Uri.parse(
            "content://com.kieronquinn.app.ambientmusicmod.settings/device_config"
        )
        val cursor = context.contentResolver.query(
            providerUri,
            null,
            null,
            null
        ) ?: return
        FLAG_VALUES = cursor.map {
            Pair(it.getString(0), it.getString(1))
        }.toMap()
        cursor.close()
    }

    fun addListener(listener: DeviceConfig.OnPropertiesChangedListener) {
        listeners.add(listener)
    }

    fun notifyChanges(context: Context, names: List<String>) {
        populateValues(context)
        val values = if(names.isNotEmpty()){
            FLAG_VALUES.filter { flag -> names.contains(flag.key) }
        }else FLAG_VALUES
        listeners.forEach {
            it.onPropertiesChanged(
                DeviceConfig.Properties::class.java.getDeclaredConstructor(
                    String::class.java, Map::class.java
                ).apply {
                    isAccessible = true
                }.newInstance(NAMESPACE, values)
            )
        }
    }

    fun getValue(key: String): String? {
        return FLAG_VALUES[key]
    }

    fun isLoggingEnabled(): Boolean {
        return getValue("NowPlaying__enable_logging")?.toBoolean() ?: false
    }

    fun getPrimaryLanguage(): String {
        return getValue("NowPlaying__device_country")!!
    }

    fun getExtraLanguages(): List<String> {
        return getValue("NowPlaying__ambient_music_extra_languages")
            ?.splitByComma() ?: emptyList()
    }

    private fun String.splitByComma(): List<String> {
        if(isBlank()) return emptyList()
        return if(!contains(",")){
            listOf(this)
        } else {
            split(",")
        }
    }

}