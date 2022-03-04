package com.donald.dps.demo

import android.content.ContentProvider
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.AppComponentFactory

@RequiresApi(Build.VERSION_CODES.P)
class HookAppComponentFactory : AppComponentFactory() {
    override fun instantiateProviderCompat(cl: ClassLoader, className: String): ContentProvider {
        Log.i(TAG, "instantiateProviderCompat $className")
        val emptyProvider = try {
            (Class.forName(className).name != className)
        } catch (e: ClassNotFoundException) {
            true
        }
        return if (emptyProvider) {
            val contentProvider = super.instantiateProviderCompat(cl, ContentProviderProxy::class.java.name) as ContentProviderProxy
            contentProvider.realContentProviderClassName = className
            contentProvider
        } else {
            super.instantiateProviderCompat(cl, className)
        }
    }
}
