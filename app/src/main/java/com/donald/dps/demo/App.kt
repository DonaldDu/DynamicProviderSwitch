package com.donald.dps.demo

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.donald.dps.lib.DynamicProviderSwitch
import com.donald.dps.lib.InstrumentationDelegate
import me.weishu.reflection.Reflection

class App : Application() {
    override fun attachBaseContext(base: Context?) {
        Reflection.unseal(base)
        InstrumentationDelegate.install()
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        Log.i(TAG, "Application onCreate")
        super.onCreate()
        providerSwitch = DynamicProviderSwitch(this, true)
        providerSwitch?.startDynamicProviders()
    }

    private var providerSwitch: DynamicProviderSwitch? = null
    override fun getResources(): Resources {
        if (providerSwitch != null) {
            providerSwitch!!.startDynamicProviders()
            if (providerSwitch!!.isFinish()) providerSwitch = null
        }
        return super.getResources()
    }
}