package com.kieronquinn.app.pixelambientmusic.utils.extensions

import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.content.ComponentName

@SuppressLint("SoonBlockedPrivateApi")
fun JobInfo.Builder.getComponent(): ComponentName {
    return JobInfo.Builder::class.java.getDeclaredField("mJobService").apply {
        isAccessible = true
    }.get(this) as ComponentName
}

@SuppressLint("SoonBlockedPrivateApi")
fun JobInfo.Builder.getId(): Int {
    return JobInfo.Builder::class.java.getDeclaredField("mJobId").apply {
        isAccessible = true
    }.get(this) as Int
}