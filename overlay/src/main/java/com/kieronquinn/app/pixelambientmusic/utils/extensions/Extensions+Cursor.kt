package com.kieronquinn.app.pixelambientmusic.utils.extensions

import android.database.Cursor

fun <T> Cursor.map(row: (Cursor) -> T): List<T> {
    moveToFirst()
    if(isAfterLast) return emptyList()
    val list = ArrayList<T>()
    do {
        list.add(row(this))
    }while (moveToNext())
    return list
}