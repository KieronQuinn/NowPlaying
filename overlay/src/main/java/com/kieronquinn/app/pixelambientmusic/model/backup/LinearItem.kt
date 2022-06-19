package com.kieronquinn.app.pixelambientmusic.model.backup

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import com.google.audio.ambientmusic.Linear
import com.google.gson.annotations.SerializedName
import com.google.protobuf.ByteString

data class LinearItem(
    @SerializedName(COLUMN_TRACK_ID)
    val trackId: String,
    @SerializedName(COLUMN_TRACK_NAME)
    val trackName: String,
    @SerializedName(COLUMN_ARTIST)
    val artist: String,
    @SerializedName(COLUMN_FLAGS)
    val flags: Long,
    @SerializedName(COLUMN_GOOGLE_ID)
    val googleId: String,
    @SerializedName(COLUMN_ID)
    val id: String,
    @SerializedName(COLUMN_FINGERPRINT)
    val fingerprint: ByteArray,
    @SerializedName(COLUMN_AUDIO_TYPE)
    val audioType: String,
    @SerializedName(COLUMN_AUDIO_DATA)
    val audioData: ByteArray
) {

    companion object {
        private const val COLUMN_TRACK_ID = "track_id"
        private const val COLUMN_TRACK_NAME = "track_name"
        private const val COLUMN_ARTIST = "artist"
        private const val COLUMN_FLAGS = "flags"
        private const val COLUMN_GOOGLE_ID = "google_id"
        private const val COLUMN_ID = "id"
        private const val COLUMN_FINGERPRINT = "fingerprint"
        private const val COLUMN_AUDIO_TYPE = "audio_type"
        private const val COLUMN_AUDIO_DATA = "audio_data"

        val COLUMNS = arrayOf(
            COLUMN_TRACK_ID,
            COLUMN_TRACK_NAME,
            COLUMN_ARTIST,
            COLUMN_FLAGS,
            COLUMN_GOOGLE_ID,
            COLUMN_ID,
            COLUMN_FINGERPRINT,
            COLUMN_AUDIO_TYPE,
            COLUMN_AUDIO_DATA
        )

        @SuppressLint("Range")
        fun fromCursor(cursor: Cursor): LinearItem = with(cursor) {
            return LinearItem(
                getString(getColumnIndex(COLUMN_TRACK_ID)),
                getString(getColumnIndex(COLUMN_TRACK_NAME)),
                getString(getColumnIndex(COLUMN_ARTIST)),
                getLong(getColumnIndex(COLUMN_FLAGS)),
                getString(getColumnIndex(COLUMN_GOOGLE_ID)),
                getString(getColumnIndex(COLUMN_ID)),
                getBlob(getColumnIndex(COLUMN_FINGERPRINT)),
                getString(getColumnIndex(COLUMN_AUDIO_TYPE)),
                getBlob(getColumnIndex(COLUMN_AUDIO_DATA))
            )
        }

        fun fromContentValues(contentValues: ContentValues): LinearItem = with(contentValues) {
            return LinearItem(
                getAsString(COLUMN_TRACK_ID),
                getAsString(COLUMN_TRACK_NAME),
                getAsString(COLUMN_ARTIST),
                getAsLong(COLUMN_FLAGS),
                getAsString(COLUMN_GOOGLE_ID),
                getAsString(COLUMN_ID),
                getAsByteArray(COLUMN_FINGERPRINT),
                getAsString(COLUMN_AUDIO_TYPE),
                getAsByteArray(COLUMN_AUDIO_DATA)
            )
        }

        fun fromLinear(linear: Linear.Tracks.TrackContainer.Track): LinearItem = with(linear) {
            return LinearItem(
                metadata.id,
                metadata.trackName,
                metadata.artist,
                metadata.flags,
                metadata.googleId,
                metadata.id2,
                fingerprint.toByteArray(),
                audioData.type,
                audioData.data.toByteArray()
            )
        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LinearItem

        if (trackId != other.trackId) return false
        if (trackName != other.trackName) return false
        if (artist != other.artist) return false
        if (flags != other.flags) return false
        if (googleId != other.googleId) return false
        if (id != other.id) return false
        if (!fingerprint.contentEquals(other.fingerprint)) return false
        if (audioType != other.audioType) return false
        if (!audioData.contentEquals(other.audioData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = trackId.hashCode()
        result = 31 * result + trackName.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + flags.hashCode()
        result = 31 * result + googleId.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + fingerprint.contentHashCode()
        result = 31 * result + audioType.hashCode()
        result = 31 * result + audioData.contentHashCode()
        return result
    }

    fun toContentValues(): ContentValues {
        return ContentValues().apply {
            put(COLUMN_TRACK_ID, trackId)
            put(COLUMN_TRACK_NAME, trackName)
            put(COLUMN_ARTIST, artist)
            put(COLUMN_FLAGS, flags)
            put(COLUMN_GOOGLE_ID, googleId)
            put(COLUMN_ID, id)
            put(COLUMN_FINGERPRINT, fingerprint)
            put(COLUMN_AUDIO_TYPE, audioType)
            put(COLUMN_AUDIO_DATA, audioData)
        }
    }

    fun toTrackContainer(): Linear.Tracks.TrackContainer {
        return Linear.Tracks.TrackContainer.newBuilder().apply {
            track = toTrack()
        }.build()
    }

    private fun toTrack(): Linear.Tracks.TrackContainer.Track {
        return Linear.Tracks.TrackContainer.Track.newBuilder().apply {
            metadata = toTrackMetadata()
            fingerprint = ByteString.copyFrom(this@LinearItem.fingerprint)
            audioData = toAudioData()
        }.build()
    }

    private fun toTrackMetadata(): Linear.Tracks.TrackContainer.Track.TrackMetadata {
        return Linear.Tracks.TrackContainer.Track.TrackMetadata.newBuilder().apply {
            id = this@LinearItem.trackId
            trackName = this@LinearItem.trackName
            artist = this@LinearItem.artist
            flags = this@LinearItem.flags
            googleId = this@LinearItem.googleId
            id2 = this@LinearItem.id
        }.build()
    }

    private fun toAudioData(): Linear.Tracks.TrackContainer.Track.AudioData {
        return Linear.Tracks.TrackContainer.Track.AudioData.newBuilder().apply {
            type = this@LinearItem.audioType
            data = ByteString.copyFrom(this@LinearItem.audioData)
        }.build()
    }

}

fun List<LinearItem>.toCursor(): MatrixCursor {
    return MatrixCursor(LinearItem.COLUMNS).apply {
        forEach {
            newRow().putAll(it.toContentValues())
        }
    }
}

private fun MatrixCursor.RowBuilder.putAll(contentValues: ContentValues) {
    contentValues.keySet().forEach {
        add(it, contentValues.get(it))
    }
}

fun List<LinearItem>.toLinearTracks(): Linear.Tracks {
    return Linear.Tracks.newBuilder().apply {
        forEach { addTrack(it.toTrackContainer()) }
    }.build()
}
