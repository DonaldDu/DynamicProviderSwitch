package com.donald.dps.demo

import android.app.Application
import android.content.res.Resources
import com.donald.dps.lib.DynamicProviderSwitch

class App : Application() {
    private val providerSwitch by lazy { DynamicProviderSwitch(this, true) }
    override fun getResources(): Resources {
//        Qigsaw.onApplicationGetResources(super.getResources())
        providerSwitch.startDynamicProviders()
        return super.getResources()
    }
}