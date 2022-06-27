package com.kieronquinn.app.pixelambientmusic.utils.extensions

import android.content.ContentProvider

fun ContentProvider.requireContextCompat() = context ?: throw Exception("Null context")