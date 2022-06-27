package com.kieronquinn.app.pixelambientmusic.providers

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.google.gson.Gson
import com.kieronquinn.app.pixelambientmusic.components.settings.SettingsStateHandler
import com.kieronquinn.app.pixelambientmusic.utils.extensions.requireContextCompat

class SettingsProvider: ContentProvider() {

    companion object {
        private const val AUTHORITY = "com.google.android.as.pam.ambientmusic.settings"
        private val URI = Uri.Builder().apply {
            scheme("content")
            authority(AUTHORITY)
        }.build()

        fun notifyUpdate(context: Context){
            context.contentResolver.notifyChange(URI, null)
        }
    }

    private val gson = Gson()

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val settings = gson.toJson(SettingsStateHandler.generateSettingsState(requireContextCompat()))
        return MatrixCursor(arrayOf("value")).apply {
            addRow(arrayOf(settings))
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        //Not supported
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        //Not supported
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        //Not supported
        return 0
    }

}