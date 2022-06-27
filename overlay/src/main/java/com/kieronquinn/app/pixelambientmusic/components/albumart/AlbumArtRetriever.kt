package com.kieronquinn.app.pixelambientmusic.components.albumart

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources.getSystem
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.audio.ambientmusic.HistoryEntry
import com.kieronquinn.app.pixelambientmusic.config.DeviceConfigOverrides
import com.kieronquinn.app.pixelambientmusic.utils.picasso.RoundedCornersTransform
import com.squareup.picasso.*
import com.squareup.picasso.Target
import java.io.File

class AlbumArtRetriever(private val context: Context) {

    companion object {
        private val YOUTUBE_MUSIC_URL_REGEX =
            "https://music.youtube.com/watch\\?v=(.*)&feature=gws_kp_track".toRegex()

        private const val TIMESTAMP_URI_SCHEME = "timestamp"
        private const val EXTRA_KEY_BITMAP = "bitmap"

        private val URI_ALBUM_ART = Uri.Builder().apply {
            scheme("content://")
            authority("com.kieronquinn.app.ambientmusicmod.albumart")
        }.build()

        @SuppressLint("StaticFieldLeak") //Application context
        private var _INSTANCE: AlbumArtRetriever? = null

        @JvmStatic
        val INSTANCE
            get() = _INSTANCE ?: throw NullPointerException("INSTANCE is not created")

        fun createInstance(context: Context) {
            _INSTANCE = AlbumArtRetriever(context)
        }
    }

    private val picasso by lazy {
        Picasso.Builder(context)
            .addRequestHandler(TimestampRequestHandler())
            .build()
    }

    private val cornerRadius by lazy {
        4.px.toFloat()
    }

    private val genericAlbumArt by lazy {
        val id = context.resources.getIdentifier(
            "album_art_dummy", "drawable", context.packageName
        )
        ContextCompat.getDrawable(context, id)!!
    }

    private val cacheDir by lazy {
        File(context.cacheDir, "album_art").apply {
            mkdirs()
        }
    }

    fun isEnabled(): Boolean {
        return DeviceConfigOverrides.getValue("NowPlaying__show_album_art") == "true"
    }

    fun clearCache() {
        cacheDir.listFiles()?.forEach { it.delete() }
        picasso.clearCache()
    }

    private fun loadTimestampInto(timestamp: Long, into: (RequestCreator) -> Unit) {
        val uri = Uri.Builder().apply {
            scheme(TIMESTAMP_URI_SCHEME)
            authority(timestamp.toString())
        }.build()
        picasso.load(uri)
            .error(genericAlbumArt)
            .placeholder(genericAlbumArt)
            .transform(RoundedCornersTransform(cornerRadius))
            .apply(into)
    }

    fun loadTimestampIntoImageView(timestamp: Long, imageView: ImageView) {
        loadTimestampInto(timestamp) {
            it.centerCrop().fit().into(imageView)
        }
    }

    fun loadTimestampIntoResult(timestamp: Long, size: Int, result: (Bitmap?) -> Unit) {
        val target = object: Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                result.invoke(bitmap)
            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                //No-op, album art is not available
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                //No-op
            }
        }
        loadTimestampInto(timestamp) {
            it.resize(size, size).centerCrop().into(target)
        }
    }

    @Synchronized
    private fun loadAlbumArt(timestamp: String): Pair<Bitmap, Picasso.LoadedFrom>? {
        val historyDb = context.getDatabasePath("history_db")
        if(!historyDb.exists()) return null
        val database = SQLiteDatabase.openDatabase(historyDb.absolutePath, null, 0)
        val query = database.rawQuery(
            "SELECT history_entry FROM recognition_history WHERE timestamp=$timestamp",
            null
        )
        query.moveToFirst()
        if(query.isAfterLast) {
            query.close()
            database.close()
            return null
        }
        val historyBlob = query.getBlob(0)
        query.close()
        database.close()
        val historyEntry = HistoryEntry.Entry.parseFrom(historyBlob)
        val youtubeId = historyEntry.trackData.streamingOptionList.firstNotNullOfOrNull {
            YOUTUBE_MUSIC_URL_REGEX.find(it.url)?.groupValues?.elementAtOrNull(1)
        } ?: return null
        getCachedAlbumArt(youtubeId)?.let {
            return Pair(it, Picasso.LoadedFrom.DISK)
        }
        getAlbumArtFromProvider(youtubeId)?.also {
            cacheAlbumArt(youtubeId, it)
        }?.let {
            return Pair(it, Picasso.LoadedFrom.NETWORK)
        }
        return null
    }

    private fun getCachedAlbumArt(youtubeId: String): Bitmap? {
        val albumArtFile = File(cacheDir, youtubeId)
        if(!albumArtFile.exists()) return null
        return try {
            BitmapFactory.decodeFile(albumArtFile.absolutePath)
        }catch (e: Exception){
            albumArtFile.delete()
            null
        }
    }

    private fun getAlbumArtFromProvider(youtubeId: String): Bitmap? {
        val result = context.contentResolver.call(URI_ALBUM_ART, youtubeId, null, null)
            ?: return null
        return result.getParcelable(EXTRA_KEY_BITMAP) as Bitmap?
    }

    private fun cacheAlbumArt(youtubeId: String, bitmap: Bitmap) {
        val albumArtFile = File(cacheDir, youtubeId)
        albumArtFile.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            it.flush()
        }
    }

    private inner class TimestampRequestHandler: RequestHandler() {

        override fun canHandleRequest(data: Request): Boolean {
            return data.uri?.scheme == TIMESTAMP_URI_SCHEME
        }

        override fun load(request: Request, networkPolicy: Int): Result? {
            val uri = request.uri
            val timestamp = uri.authority ?: return null
            return loadAlbumArt(timestamp)?.let {
                Result(it.first, it.second)
            }
        }

    }

    private val Int.px: Int get() = (this * getSystem().displayMetrics.density).toInt()

}
