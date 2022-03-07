package com.donald.dps.lib

import android.content.ContentProvider
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.AppComponentFactory

@RequiresApi(Build.VERSION_CODES.P)
open class HookProviderFactory : AppComponentFactory() {
    private val TAG = "ProviderSwitch"
    override fun instantiateProviderCompat(cl: ClassLoader, className: String): ContentProvider {
        if (BuildConfig.DEBUG) Log.i(TAG, "instantiateProviderCompat $className")
        val emptyProvider = try {
            (Class.forName(className).name != className)
        } catch (e: ClassNotFoundException) {
            true
        }
        return if (emptyProvider) {
            if (BuildConfig.DEBUG) Log.i(TAG, "ContentProviderProxy $className")
            super.instantiateProviderCompat(cl, ContentProviderProxy::class.java.name)
        } else {
            super.instantiateProviderCompat(cl, className)
        }
    }
}
