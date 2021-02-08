package com.donald.dps.lib

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager.*
import android.content.pm.ProviderInfo
import android.net.Uri

class DynamicProviderSwitch(private val context: Context, private val log: Boolean) {
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

    private fun Context.startDynamicProvider(provider: ProviderInfo): Boolean {
        val componentName = try {
            val p = Class.forName(provider.name)
            ComponentName(this, p.name)
        } catch (e: Exception) {
            null
        }
        return if (componentName != null) {
            packageManager.setComponentEnabledSetting(componentName, COMPONENT_ENABLED_STATE_ENABLED, DONT_KILL_APP)
            val uri = Uri.parse("content://${provider.authority}")
            try {
                contentResolver.insert(uri, null)
            } catch (e: Exception) {
            }
            packageManager.setComponentEnabledSetting(componentName, COMPONENT_ENABLED_STATE_DISABLED, DONT_KILL_APP)
            if (log) println("startProvider: ${provider.name}, ok")
            true
        } else {
            if (log) println("startProvider: ${provider.name}, uninstalled")
            false
        }
    }

    /**
     * after preloadInstalledSplits
     * */
    private fun Context.dynamicProviders(): MutableList<ProviderInfo> {
        @Suppress("DEPRECATION")
        val disabled = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            MATCH_DISABLED_COMPONENTS
        } else GET_DISABLED_COMPONENTS
        val info = packageManager.getPackageInfo(applicationInfo.packageName, GET_PROVIDERS or disabled)
        val ps = info.providers?.filter {
            !it.enabled && it.authority != null
        } ?: emptyList()
        return ps.toMutableList()
    }
}




