package com.kieronquinn.app.pixelambientmusic.providers

import android.content.*
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.google.audio.ambientmusic.Linear
import com.google.audio.ambientmusic.ShardTracks
import com.kieronquinn.app.pixelambientmusic.components.settings.SettingsStateHandler
import com.kieronquinn.app.pixelambientmusic.utils.extensions.getVersion
import com.kieronquinn.app.pixelambientmusic.utils.extensions.requireContextCompat
import org.iq80.leveldb.table.BytewiseComparator
import org.iq80.leveldb.table.FileChannelTable
import org.json.JSONArray
import java.io.File

class LevelDbProvider: ContentProvider() {

    companion object {
        const val PAM_FOLDER = "pixel_ambient_music"
        private const val AUTHORITY = "com.google.android.as.pam.ambientmusic.leveldbprovider"
        private const val SHARD_CORE = "matcher_tah.leveldb"
        private const val SUPERPACKS_FOLDER = "superpacks"
        private const val AMBIENT_SUPERPACKS_FOLDER = "ambientmusic-index-17_09_02"

        private const val COLUMN_DB_ID = "db_id"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TRACK_NAME = "track_name"
        private const val COLUMN_ARTIST = "artist"
        private const val COLUMN_GOOGLE_ID = "google_id"
        private const val COLUMN_PLAYERS = "players"
        private const val COLUMN_ALBUM = "album"
        private const val COLUMN_YEAR = "year"

        private const val CACHE_VERSION = "cache_version"

        private val TRACK_HEADER_SECOND_BYTE = arrayOf((0x0B).toByte(), (0x1B).toByte())

        private const val PATH_DOWNLOAD_STATE = "downloadstate"

        private val URI_DOWNLOAD_STATE = Uri.Builder().apply {
            scheme("content")
            authority(AUTHORITY)
            path(PATH_DOWNLOAD_STATE)
        }.build()

        fun notifyUpdate(context: Context){
            context.contentResolver.notifyChange(URI_DOWNLOAD_STATE, null)
        }
    }

    private enum class Method {
        LIST, GET, COUNT, LINEAR, COUNTRY, DOWNLOAD_STATE;

        companion object {
            fun getMethod(code: Int): Method? {
                return values().firstOrNull { it.ordinal == code }
            }
        }
    }

    private val cachePrefs by lazy {
        requireContextCompat().getSharedPreferences("${requireContextCompat().packageName}_countcache", Context.MODE_PRIVATE)
    }

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, "list", Method.LIST.ordinal)
        addURI(AUTHORITY, "get/*", Method.GET.ordinal)
        addURI(AUTHORITY, "count/*", Method.COUNT.ordinal)
        addURI(AUTHORITY, "count/*/*", Method.COUNT.ordinal)
        addURI(AUTHORITY, "linear/*", Method.LINEAR.ordinal)
        addURI(AUTHORITY, "country", Method.COUNTRY.ordinal)
        addURI(AUTHORITY, PATH_DOWNLOAD_STATE, Method.DOWNLOAD_STATE.ordinal)
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return when(Method.getMethod(uriMatcher.match(uri))){
            Method.LIST -> getDatabaseListCursor()
            Method.GET -> getDatabaseCursor(uri)
            Method.COUNT -> getCount(uri)
            Method.LINEAR -> getLinear(uri)
            Method.COUNTRY -> getCountry()
            Method.DOWNLOAD_STATE -> getSuperpacksDownloadCount()
            else -> null
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

    private fun getDatabaseListCursor(): Cursor {
        return MatrixCursor(arrayOf("name")).apply {
            getLevelDbFiles().forEach {
                addRow(arrayOf(it.name))
            }
        }
    }

    private fun getDatabaseCursor(uri: Uri): Cursor? {
        val name = uri.pathSegments.elementAtOrNull(1) ?: return null
        return loadDatabaseIntoCursor(name)
    }

    private fun getCount(uri: Uri): Cursor? {
        val type = uri.pathSegments.elementAtOrNull(1) ?: return null
        return when(type){
            "leveldb" -> getLevelDbCount()
            "linear" -> {
                val name = uri.pathSegments.elementAtOrNull(2) ?: return null
                getLinearCount(name)
            }
            else -> null
        }
    }

    /**
     *  Gets the count of tracks in the Level DB databases. This is backed by a cache, based on
     *  a hash of the list of shards, since calculating the count takes about 2 seconds.
     */
    private fun getLevelDbCount(): MatrixCursor {
        val createCursor = { count: Int ->
            MatrixCursor(arrayOf("count")).apply {
                addRow(arrayOf(count))
            }
        }
        val files = getLevelDbFiles()
        val hash = files.map { it.name }.hashCode()
        cachePrefs.getCachedCount(hash)?.let {
            return createCursor(it)
        }
        val count = files.map { loadDatabase(it) }.flatten().groupBy { it.dbId }.size
        return createCursor(count).also {
            cachePrefs.commitCachedCount(hash, count)
        }
    }

    private fun getLevelDbCountry(): String? {
        val superpacksDir = File(requireContextCompat().filesDir, SUPERPACKS_FOLDER)
        if(!superpacksDir.exists()) return null
        val ambientDir = File(superpacksDir, AMBIENT_SUPERPACKS_FOLDER)
        if(!ambientDir.exists()) return null
        val oldestShard = ambientDir.listFiles()?.filterNot {
            it.name.startsWith("cc")
        }?.minByOrNull { it.lastModified() } ?: return null
        return oldestShard.name.substring(0, 2)
    }

    private fun getLevelDbFiles(): List<File> {
        val files = ArrayList<File>()
        val pamDir = File(requireContextCompat().filesDir, PAM_FOLDER)
        if(!pamDir.exists()) return files
        val levelDbCountry = getLevelDbCountry()
        val coreShard = File(pamDir, SHARD_CORE)
        if(coreShard.exists()){
            files.add(coreShard)
        }
        val superpacksDir = File(requireContextCompat().filesDir, SUPERPACKS_FOLDER)
        if(!superpacksDir.exists()) return files
        val ambientDir = File(superpacksDir, AMBIENT_SUPERPACKS_FOLDER)
        if(!ambientDir.exists()) return files
        val packs = ambientDir.listFiles()?.filterNot { it.name.startsWith("cc-") }?.let {
            if(levelDbCountry != null){
                it.filter { file -> file.name.startsWith(levelDbCountry) }
            }else it
        }
        files.addAll(packs ?: emptyList())
        return files
    }

    private fun loadDatabase(file: File): ArrayList<ShardTracks.Track> = try {
        val stream = file.inputStream()
        val channel = stream.channel
        val database =  FileChannelTable(file.name, channel, BytewiseComparator(), true)
        val tracks = ArrayList<ShardTracks.Track>()
        database.iterator().forEach {
            val track = parseTrackProto(it.value.bytes) ?: return@forEach
            tracks.add(track)
        }
        tracks
    }catch (e: Exception){
        //Corrupt file
        ArrayList()
    }

    private fun loadDatabase(name: String): List<List<ShardTracks.Track>> {
        return getLevelDbFiles().filter { it.name == name }.map { loadDatabase(it) }
    }

    private fun loadDatabaseIntoCursor(name: String): MatrixCursor {
        val databases = loadDatabase(name)
        val cursor = MatrixCursor(
            arrayOf(
                COLUMN_DB_ID,
                COLUMN_ID,
                COLUMN_TRACK_NAME,
                COLUMN_ARTIST,
                COLUMN_GOOGLE_ID,
                COLUMN_PLAYERS,
                COLUMN_ALBUM,
                COLUMN_YEAR
            )
        )
        databases.merge().forEach { track ->
            val row = arrayOf(
                track.dbId,
                track.id,
                track.trackName,
                track.artist,
                track.googleId,
                track.playerList.toJsonArray().toString(),
                track.album,
                track.year
            )
            cursor.addRow(row)
        }
        return cursor
    }

    private fun parseTrackProto(bytes: ByteArray): ShardTracks.Track? {
        //Attempt to differentiate between a track and audio data by reading the header
        if(bytes.size < 2) return null
        if(bytes[0] != (0x0A).toByte() || !TRACK_HEADER_SECOND_BYTE.contains(bytes[1])) return null
        //Passed the basic header check, attempt to parse and return null if it fails
        return try {
            ShardTracks.Track.parseFrom(bytes)
        }catch (e: Exception){
            null
        }
    }

    private fun List<ShardTracks.Track.Player>.toJsonArray(): JSONArray {
        return JSONArray().apply {
            this@toJsonArray.forEach {
                put(it.url)
            }
        }
    }

    private fun List<List<ShardTracks.Track>>.merge(): List<ShardTracks.Track> {
        //Merge the lists, then group by the shared ID and create the best track
        return flatten().groupBy {
            it.dbId
        }.map {
            it.value.createBest()
        }
    }

    private fun List<ShardTracks.Track>.createBest(): ShardTracks.Track {
        return ShardTracks.Track.newBuilder().apply {
            dbId = first().dbId
            id = first().id
            trackName = first().trackName
            artist = first().artist
            googleId = first().googleId
            addAllPlayer(
                firstOrNull { it.playerList.isNotEmpty() }?.playerList
                    ?: emptyList<ShardTracks.Track.Player>()
            )
            album = firstOrNull { it.album != null }?.album
            year = firstOrNull { it.year != 0 }?.year ?: 0
        }.build()
    }

    private fun getCountry(): Cursor? {
        val country = getLevelDbCountry() ?: return null
        return MatrixCursor(arrayOf("country")).apply {
            addRow(arrayOf(country))
        }
    }

    private fun getSuperpacksDownloadCount(): Cursor {
        return SettingsStateHandler.getSuperpackDownloadCount(requireContextCompat()).let {
            MatrixCursor(arrayOf("count")).apply {
                addRow(arrayOf(it))
            }
        }
    }

    private fun getLinear(uri: Uri): Cursor? {
        val name = uri.pathSegments.elementAtOrNull(1) ?: return null
        return loadLinearIntoCursor(name)
    }

    private fun loadLinear(name: String): Linear.Tracks? {
        val file = File(requireContextCompat().cacheDir, name)
        if(!file.exists()) return null
        return Linear.Tracks.parseFrom(file.readBytes())
    }

    private fun loadLinearIntoCursor(name: String): Cursor? {
        val linear = loadLinear(name) ?: return null
        val cursor = MatrixCursor(
            arrayOf(
                COLUMN_DB_ID,
                COLUMN_ID,
                COLUMN_TRACK_NAME,
                COLUMN_ARTIST,
                COLUMN_GOOGLE_ID,
                COLUMN_PLAYERS,
                COLUMN_ALBUM,
                COLUMN_YEAR
            )
        )
        linear.trackList.forEach { track ->
            val metadata = track.track.metadata
            val row = arrayOf(
                metadata.id,
                metadata.id2,
                metadata.trackName,
                metadata.artist,
                metadata.googleId,
                emptyList<String>(),
                null,
                0
            )
            cursor.addRow(row)
        }
        return cursor
    }

    private fun getLinearCount(name: String): Cursor? {
        val linear = loadLinear(name) ?: return null
        return MatrixCursor(arrayOf("count")).apply {
            addRow(arrayOf(linear.trackCount))
        }
    }

    private fun SharedPreferences.getCachedCount(hashCode: Int): Int? {
        if(!contains(hashCode.toString())){
            clearCachedCounts()
            return null
        }
        //Force an update if the Now Playing version has changed
        if(cachePrefs.getLong(CACHE_VERSION, 0) != requireContextCompat().getVersion()){
            clearCachedCounts()
            return null
        }
        return getInt(hashCode.toString(), -1).let {
            if(it == -1) null
            else it
        }
    }

    private fun SharedPreferences.clearCachedCounts() {
        edit().clear().commit()
    }

    private fun SharedPreferences.commitCachedCount(hashCode: Int, count: Int) {
        edit().putInt(hashCode.toString(), count)
            .putLong(CACHE_VERSION, requireContextCompat().getVersion())
            .commit()
    }

}