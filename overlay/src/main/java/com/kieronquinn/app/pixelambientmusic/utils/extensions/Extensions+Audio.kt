package com.kieronquinn.app.pixelambientmusic.utils.extensions

import java.nio.ByteBuffer
import java.nio.ByteOrder.BIG_ENDIAN
import java.nio.ByteOrder.LITTLE_ENDIAN

fun ByteArray.toShortArray(bigEndian: Boolean): ShortArray {
    return ShortArray(this.size / 2).apply {
        ByteBuffer.wrap(this@toShortArray)
            .order(if(bigEndian) BIG_ENDIAN else LITTLE_ENDIAN).asShortBuffer()[this]
    }
}

fun ShortArray.applyGain(gain: Float): ShortArray {
    if (isNotEmpty()) {
        for (i in indices) {
            this[i] = (this[i] * gain).toInt().coerceAtMost(Short.MAX_VALUE.toInt()).toShort()
        }
    }
    return this
}