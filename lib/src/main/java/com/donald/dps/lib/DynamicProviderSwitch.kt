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
    filter: ((ProviderInfo) -> Boolean)? = null
) {
    private val TAG = "ProviderSwitch"
    private val filter: (ProviderInfo) -> Boolean = filter ?: {
        if (log) Log.i(TAG, "filter -> ${it.name}")
        it.isDisabled
    }

    init {
        compatDynamicProvider()
    }

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

    private fun compatDynamicProvider() {
        HookUtil.compatDynamicProvider(context)
    }

    fun isFinish(): Boolean {
        return providers.isEmpty()
    }

    /**
     * @return start successful or not
     * */
    private fun Context.startProvider(provider: ProviderInfo): Boolean {
        ComponentName(this, provider.name).enable(context, true)
        return try {
            assert(Class.forName(provider.name).name.isNotEmpty())
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
        }
    }

    private fun Context.providers(): MutableList<ProviderInfo> {
        @Suppress("DEPRECATION")
        val disabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) MATCH_DISABLED_COMPONENTS else GET_DISABLED_COMPONENTS
        val info = packageManager.getPackageInfo(packageName, GET_PROVIDERS or disabled)
        val ps = info.providers?.filter(filter) ?: emptyList()
        return ps.toMutableList()
    }
}

internal fun ComponentName.enable(context: Context, enabled: Boolean) {
    val newState = if (enabled) COMPONENT_ENABLED_STATE_ENABLED else COMPONENT_ENABLED_STATE_DISABLED
    context.packageManager.setComponentEnabledSetting(this, newState, DONT_KILL_APP)
}

internal val ProviderInfo.isDisabled: Boolean
    get() {
        return if (authority != null) {
            try {
                !enabled || Class.forName(name).name.isEmpty()
            } catch (e: Exception) {
                true
            }
        } else false
    }

