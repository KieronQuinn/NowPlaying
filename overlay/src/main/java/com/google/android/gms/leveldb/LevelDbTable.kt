package com.google.android.gms.leveldb

object LevelDbTable {

    init {
        System.loadLibrary("leveldbjni")
    }

    external fun nativeOpen(file: String): Long
    external fun nativeGet(pointer: Long, bytes: ByteArray): ByteArray
    external fun nativeClose(pointer: Long)

}