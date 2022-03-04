package com.donald.dps.demo

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.annotation.Keep

@Keep
class EmptyProvider : ContentProvider() {
    init {
        Log.i(TAG, "${javaClass.name} init")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.i(TAG, "${javaClass.name} insert")
        return null
    }

    override fun onCreate(): Boolean {
        Log.i(TAG, "${javaClass.name} onCreate currentThread:" + Thread.currentThread().id)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return null
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }
}