package com.kieronquinn.app.pixelambientmusic.components.legacy

import com.google.audio.ambientmusic.LegacyRecognitionResult.LegacyResult
import com.google.audio.ambientmusic.RecognitionResult.Result
import java.io.File

object LegacyRecognitionConverter {

    private var shardList = emptyList<String>()

    fun setLastShardPaths(shardPaths: Array<String>) {
        shardList = shardPaths.map { it.extractNameFromPath() }
    }

    fun convertResult(result: LegacyResult): Result {
        return Result.newBuilder().apply {
            addAllTracks(result.tracksList.mapIndexed { index, track ->
                track.convert(index == 0 && result.isRecognised)
            })
            status = if(result.isRecognised){
                Result.Status.MUSIC_RECOGNIZED
            } else {
                Result.Status.MUSIC_UNRECOGNIZED
            }
            result.fudgeFooter()?.let {
                statusFooter = it
            }
        }.build()
    }

    private fun LegacyResult.Track.convert(isMatch: Boolean): Result.Track {
        val legacy = this
        return Result.Track.newBuilder().apply {
            metadata = legacy.metadata.convert()
            flags = legacy.flags
            flags2 = legacy.flags2
            flags3 = legacy.flags3
            time = 1
            shard = shardForIndex(shardIndex)
            this.isMatch = isMatch
        }.build()
    }

    private fun LegacyResult.Track.TrackMetadata.convert(): Result.Track.TrackMetadata {
        val legacy = this
        return Result.Track.TrackMetadata.newBuilder().apply {
            id1 = legacy.id1
            id2 = legacy.id2
            trackName = legacy.trackName
            artist = legacy.artist
            flags = legacy.flags
            googleId = legacy.googleId
            id3 = legacy.id3
            id4 = legacy.id4
            album = legacy.album
            year = legacy.year
            addAllData(legacy.dataList)
            shortGoogleId = legacy.shortGoogleId
        }.build()
    }

    private fun LegacyResult.fudgeFooter(): Result.StatusFooter? {
        if(!isRecognised) return null
        return Result.StatusFooter.newBuilder().apply {
            val recognisedTrack = tracksList.firstOrNull() ?: return null
            val convertedShard = shardForIndex(recognisedTrack.shardIndex)
            data = Result.StatusFooter.Data.newBuilder().apply {
                value = 200000
                shard = convertedShard
                flags = -1056382829
            }.build()
            otherData = Result.StatusFooter.OtherData.newBuilder().apply {
                flags = 1065267674
            }.build()
        }.build()
    }

    private fun String.extractNameFromPath(): String {
        return File(this).name.let {
            if(it == "matcher_tah.leveldb") ".core"
            else it
        }
    }

    private fun shardForIndex(index: Int): String {
        //Default to core shard if not found
        return shardList.elementAtOrNull(index) ?: ".core"
    }

}