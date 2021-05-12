package com.donald.dps.lib

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager.*
import android.content.pm.ProviderInfo
import android.net.Uri
import android.util.Log

class DynamicProviderSwitch(private val context: Context, private val log: Boolean) {
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

    private fun Context.startProvider(provider: ProviderInfo): Boolean {
        val component = provider.toExistComponent(this)
        return if (component != null) {
            component.enabled = true
            val uri = Uri.parse("content://${provider.authority}")
            try {
                contentResolver.insert(uri, null)
            } catch (e: Throwable) {
                component.enabled = false//disabled when error
                if (log) e.printStackTrace()
            }
            if (log) Log.i(TAG, "startProvider: ${provider.name}, ok")
            true
        } else {
            if (log) Log.i(TAG, "startProvider: ${provider.name}, uninstalled")
            false
        }
    }

    private fun ProviderInfo.toExistComponent(context: Context): ComponentName? {
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

    private fun Context.providers(): MutableList<ProviderInfo> {
        val info = packageManager.getPackageInfo(packageName, GET_PROVIDERS)
        val ps = info.providers?.filter { it.authority != null } ?: emptyList()
        return ps.toMutableList()
    }
}




