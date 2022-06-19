package com.kieronquinn.app.pixelambientmusic.utils.extensions

import android.app.Notification

fun Notification.Builder.getChannelId(): String? {
    val notification = this::class.java.getDeclaredField("mN").apply {
        isAccessible = true
    }.get(this) as Notification
    return notification.channelId
}

fun Notification.Builder.getContentTitle(): CharSequence? {
    val notification = this::class.java.getDeclaredField("mN").apply {
        isAccessible = true
    }.get(this) as Notification
    return notification.extras.getCharSequence(Notification.EXTRA_TITLE)
}