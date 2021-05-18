package com.donald.dps.lib

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager.*
import android.content.pm.ProviderInfo
import android.net.Uri
import android.os.Build
import android.util.Log

open class DynamicProviderSwitch(
    private val context: Context,
    private val log: Boolean,
    private val filter: (ProviderInfo) -> Boolean = { it.authority != null }
) {
    private val TAG = "ProviderSwitch"
    private val providers = context.providers()

    /**
     * called in Application.getResources
     * */
    fun startDynamicProviders() {
        if (providers.isEmpty()) return
        val iterator = providers.iterator()
        while (iterator.hasNext()) {
            val provider = iterator.next()
            if (context.startProvider(provider)) iterator.remove()
        }
    }

    fun isFinish(): Boolean {
        return providers.isEmpty()
    }

    /**
     * @return start successful or not
     * */
    private fun Context.startProvider(provider: ProviderInfo): Boolean {
        val component = ComponentName(this, provider.name)
        return try {
            component.enabled = false
            assert(Class.forName(provider.name).name == provider.name)
            component.enabled = true
            val uri = Uri.parse("content://${provider.authority}")
            contentResolver.insert(uri, null)
            if (log) Log.i(TAG, "startProvider: ${provider.name}, ok")
            true
        } catch (e: Throwable) {
            when (e) {
                is ClassNotFoundException -> false
                is UnsupportedOperationException -> {
                    if (log) Log.i(TAG, "startProvider: ${provider.name}, ok")
                    true
                }
                else -> {
                    if (log) {
                        Log.i(TAG, "startProvider: ${provider.name}, ok")
                        e.printStackTrace()
                    }
                    true
                }
            }
        } finally {
            component.enabled = false
        }
    }

    private var ComponentName.enabled: Boolean
        get() = false
        set(value) {
            val newState = if (value) COMPONENT_ENABLED_STATE_ENABLED else COMPONENT_ENABLED_STATE_DISABLED
            context.packageManager.setComponentEnabledSetting(this, newState, DONT_KILL_APP)
        }

    private fun Context.providers(): MutableList<ProviderInfo> {
        @Suppress("DEPRECATION")
        val disabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) MATCH_DISABLED_COMPONENTS else GET_DISABLED_COMPONENTS
        val info = packageManager.getPackageInfo(packageName, GET_PROVIDERS or disabled)
        val ps = info.providers?.filter(filter) ?: emptyList()
        return ps.toMutableList()
    }
}




