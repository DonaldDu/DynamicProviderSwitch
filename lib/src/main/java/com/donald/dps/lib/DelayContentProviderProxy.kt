package com.donald.dps.lib

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class DelayContentProviderProxy : ContentProviderProxy() {
    private var isProviderEnable = false

    @RequiresApi(Build.VERSION_CODES.P)
    override fun isRealProviderOK(): Boolean {
        if (!isProviderEnable) {
            isProviderEnable = DelayProviderFactory.isProviderEnable
        }
        if (isProviderEnable && realContentProvider == null) {
            createAndActivateRealContentProvider(javaClass.classLoader)
        }
        return isProviderEnable
    }

    override fun createAndActivateRealContentProvider(classLoader: ClassLoader?) {
        Log.i(TAG, "createAndActivateRealContentProvider $realContentProviderClassName")
        val start = System.currentTimeMillis()
        super.createAndActivateRealContentProvider(classLoader)
        val end = System.currentTimeMillis()
        Log.i(TAG, "createAndActivateRealContentProvider $realContentProviderClassName, cost ${end - start}")
    }
}