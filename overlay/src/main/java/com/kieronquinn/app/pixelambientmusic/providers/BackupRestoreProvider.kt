package com.kieronquinn.app.pixelambientmusic.providers

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.google.audio.ambientmusic.Linear
import com.kieronquinn.app.pixelambientmusic.model.backup.LinearItem
import com.kieronquinn.app.pixelambientmusic.model.backup.toCursor
import com.kieronquinn.app.pixelambientmusic.utils.extensions.requireContextCompat
import java.io.File

class BackupRestoreProvider: ContentProvider() {

    companion object {
        private const val AUTHORITY = "com.google.android.as.pam.ambientmusic.backuprestoreprovider"

        private const val TABLE_HISTORY = "recognition_history"
        private val HISTORY_COLUMNS = arrayOf(
            "timestamp", //long
            "history_entry", //blob
            "track_id", //string
            "artist", //string
            "title", //string
            "fingerprints", //blob
            "shards_region", //string
            "downloaded_shards_version", //integer
            "core_shard_version" //integer
        )

        private const val TABLE_FAVOURITES = "favorites"
        private val FAVOURITES_COLUMNS = arrayOf(
            "track_id", //string
            "timestamp" //long
        )

    }

    private val databaseFile by lazy {
        requireContextCompat().getDatabasePath("history_db")
    }

    private val linearFile by lazy {
        File(requireContextCompat().cacheDir, "linear.db")
    }

    private val linearv3File by lazy {
        File(requireContextCompat().cacheDir, "linear_v3.db")
    }

    private enum class Table {
        HISTORY, FAVOURITES, LINEAR, LINEAR_V3;

        companion object {
            fun getTable(code: Int): Table? {
                return values().firstOrNull { it.ordinal == code }
            }
        }
    }

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, "history", Table.HISTORY.ordinal)
        addURI(AUTHORITY, "favourites", Table.FAVOURITES.ordinal)
        addURI(AUTHORITY, "linear", Table.LINEAR.ordinal)
        addURI(AUTHORITY, "linear_v3", Table.LINEAR_V3.ordinal)
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        when(Table.getTable(uriMatcher.match(uri))){
            Table.HISTORY -> insertIntoTable(TABLE_HISTORY, values!!)
            Table.FAVOURITES -> insertIntoTable(TABLE_FAVOURITES, values!!)
            Table.LINEAR -> insertIntoLinear(false, values!!)
            Table.LINEAR_V3 -> insertIntoLinear(true, values!!)
            else -> {} //No-op
        }
        return uri
    }

    private fun insertIntoTable(table: String, values: ContentValues) = runWithDatabase {
        this.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private fun insertIntoLinear(v3: Boolean, values: ContentValues){
        val linearDb = if(v3){
            linearv3File
        }else linearFile
        val current = if(linearDb.exists()){
            Linear.Tracks.parseFrom(linearDb.readBytes()).toBuilder()
        } else Linear.Tracks.newBuilder()
        val track = LinearItem.fromContentValues(values).toTrackContainer()
        val dupe = current.trackList.indexOfFirst {
            it.track.metadata.id == track.track.metadata.id
        }
        if(dupe != -1){
            current.removeTrack(dupe)
        }
        current.addTrack(track)
        linearFile.writeBytes(current.build().toByteArray())
    }

    override fun delete(uri: Uri, queryString: String?, queryArgs: Array<out String>?): Int {
        when(Table.getTable(uriMatcher.match(uri))){
            Table.HISTORY -> deleteTableRows(TABLE_HISTORY)
            Table.FAVOURITES -> deleteTableRows(TABLE_FAVOURITES)
            Table.LINEAR -> deleteLinear(false)
            Table.LINEAR_V3 -> deleteLinear(true)
            else -> {} //No-op
        }
        return 0
    }

    private fun deleteTableRows(table: String) = runWithDatabase {
        this.delete(table, null, null)
    }

    private fun deleteLinear(v3: Boolean) {
        if(v3){
            linearv3File.deleteIfExists()
        }else{
            linearFile.deleteIfExists()
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return when(Table.getTable(uriMatcher.match(uri))){
            Table.HISTORY -> getRecognitionHistory()
            Table.FAVOURITES -> getFavourites()
            Table.LINEAR -> getLinear(false)
            Table.LINEAR_V3 -> getLinear(true)
            else -> null
        }
    }

    private fun getRecognitionHistory(): Cursor? = runWithDatabase(false) {
        this.query(
            TABLE_HISTORY,
            HISTORY_COLUMNS,
            null,
            null,
            null,
            null,
            null
        )
    }

    private fun getFavourites(): Cursor? = runWithDatabase(false) {
        this.query(
            TABLE_FAVOURITES,
            FAVOURITES_COLUMNS,
            null,
            null,
            null,
            null,
            null
        )
    }

    private fun getLinear(v3: Boolean): Cursor? {
        val linearDb = if(v3){
            linearv3File
        }else linearFile
        if(!linearDb.exists()) return null
        val linear = Linear.Tracks.parseFrom(linearDb.readBytes())
        return linear.trackList.map { LinearItem.fromLinear(it.track) }.toCursor()
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        queryString: String?,
        queryArgs: Array<out String>?
    ): Int {
        return 0 //Unsupported
    }

    @Synchronized
    private fun <T> runWithDatabase(close: Boolean = true, block: SQLiteDatabase.() -> T): T? {
        if (!databaseFile.exists()) return null
        val database = SQLiteDatabase.openDatabase(databaseFile.absolutePath, null, 0)
        return block(database).also {
            if(close) database.close()
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    private fun File.deleteIfExists() {
        if(exists()) delete()
    }

}