package com.kieronquinn.app.pixelambientmusic.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AudioPackage(val packageName: String, val label: CharSequence): Parcelable