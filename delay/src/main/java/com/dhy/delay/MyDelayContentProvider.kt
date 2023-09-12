package com.dhy.delay

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

class MyDelayContentProvider : ContentProvider() {
    companion object {
        var started = false
        const val delayMS = 9000L
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun onCreate(): Boolean {
        val start = System.currentTimeMillis()
        Thread.sleep(delayMS)
        val end = System.currentTimeMillis()
        println("MyDerectContentProvider, cost ${end - start}")
        started = true
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? = null

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0
}