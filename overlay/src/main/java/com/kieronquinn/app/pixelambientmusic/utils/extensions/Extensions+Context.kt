package com.kieronquinn.app.pixelambientmusic.utils.extensions

import android.content.Context

fun Context.dumpToFile(name: String, bytes: ByteArray) {
    openFileOutput(name, Context.MODE_PRIVATE).use { out ->
        out.write(bytes)
        out.flush()
    }
}