package com.donald.dps.demo

import android.app.Application
import android.content.res.Resources
import com.donald.dps.lib.DynamicProviderSwitch

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        providerSwitch = DynamicProviderSwitch(this, true)
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