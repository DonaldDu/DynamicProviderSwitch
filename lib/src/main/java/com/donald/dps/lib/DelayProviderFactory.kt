package com.donald.dps.lib

import android.content.ContentProvider
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.AppComponentFactory
import com.donald.dps.lib.ContentProviderProxy.TAG


@RequiresApi(Build.VERSION_CODES.P)
class DelayProviderFactory : AppComponentFactory() {
    companion object {
        private var it: DelayProviderFactory? = null

        /**
         * 是否启用直接启动Provider功能
         * */
        var derectProvider = false

        /**
         * 取消延迟启动，需要在Application.attachBaseContext中调用，加入需要取消延迟启动的名称
         * */
        @JvmStatic
        val derectProviderNames: MutableSet<String> = mutableSetOf()
        private val providers: MutableList<DelayContentProviderProxy> = mutableListOf()

        @JvmStatic
        val isProviderEnable: Boolean get() = it?.enableProvider ?: false

        @JvmStatic
        fun enableProvider(delayMS: Long? = 300) {
            if (isProviderEnable) return
            it?.enableProvider = true
            if (delayMS != null) {
                Handler(Looper.getMainLooper()).postDelayed({
                    enableProviderInner()
                }, delayMS)
            } else {
                enableProviderInner()
            }
        }

        private fun enableProviderInner() {
            startInitializationProvider()
            runInIoScope {
                startProviders()
                if (providers.isNotEmpty()) runInMainScope { startProviders() }
            }
        }

        private fun startProviders() {
            val iterator = providers.iterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                if (p.safeTouch()) iterator.remove()
            }
        }

        private fun startInitializationProvider() {
            val name = "androidx.startup.InitializationProvider"
            val p = providers.find { it.realContentProviderClassName == name }
            if (p != null) {
                providers.remove(p)
                p.safeTouch()
            }
        }

        private fun ContentProviderProxy?.safeTouch(): Boolean {
            val p = this ?: return false
            return try {
                p.isRealProviderOK
            } catch (e: Exception) {
                e.printStackTrace()
                true
            }
        }
    }

    private var enableProvider = false

    init {
        it = this
    }

    override fun instantiateProviderCompat(cl: ClassLoader, className: String): ContentProvider {
        Log.i(TAG, "instantiateProviderCompat $className")
        return if (derectProvider || derectProviderNames.contains(className)) {
            super.instantiateProviderCompat(cl, className)
        } else {
            val provider = super.instantiateProviderCompat(cl, DelayContentProviderProxy::class.java.name)
            providers.add(provider as DelayContentProviderProxy)
            provider
        }
    }
}
