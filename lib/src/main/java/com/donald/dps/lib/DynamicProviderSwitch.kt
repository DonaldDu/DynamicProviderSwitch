package com.donald.dps.lib

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager.*
import android.content.pm.ProviderInfo
import android.net.Uri
import android.os.Build
import android.util.Log

class DynamicProviderSwitch(private val context: Context, private val log: Boolean) {
    private val TAG = "ProviderSwitch"
    private val providers = context.dynamicProviders()

    /**
     * called in Application.getResources
     * */
    fun startDynamicProviders() {
        if (providers.isEmpty()) return
        val iterator = providers.iterator()
        while (iterator.hasNext()) {
            val provider = iterator.next()
            if (context.startDynamicProvider(provider)) iterator.remove()
        }
    }

    fun isFinish(): Boolean {
        return providers.isEmpty()
    }

    private fun Context.startDynamicProvider(provider: ProviderInfo): Boolean {
        val component = provider.toComponent(this)
        return if (component != null) {
            component.enabled = true
            val uri = Uri.parse("content://${provider.authority}")
            try {
                contentResolver.insert(uri, null)
            } catch (e: Exception) {
            }
            component.enabled = false
            if (log) Log.i(TAG, "startProvider: ${provider.name}, ok")
            true
        } else {
            if (log) Log.i(TAG, "startProvider: ${provider.name}, uninstalled")
            false
        }
    }

    private fun ProviderInfo.toComponent(context: Context): ComponentName? {
        return try {
            val p = Class.forName(name)
            ComponentName(context, p.name)
        } catch (e: Exception) {
            null
        }
    }

    private var ComponentName.enabled: Boolean
        get() = false
        set(value) {
            val newState = if (value) COMPONENT_ENABLED_STATE_ENABLED else COMPONENT_ENABLED_STATE_DISABLED
            context.packageManager.setComponentEnabledSetting(this, newState, DONT_KILL_APP)
        }

    private fun Context.dynamicProviders(): MutableList<ProviderInfo> {
        @Suppress("DEPRECATION")
        val disabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) MATCH_DISABLED_COMPONENTS else GET_DISABLED_COMPONENTS
        val info = packageManager.getPackageInfo(packageName, GET_PROVIDERS or disabled)
        val ps = info.providers?.filter { !it.enabled && it.authority != null } ?: emptyList()
        return ps.toMutableList()
    }
}




