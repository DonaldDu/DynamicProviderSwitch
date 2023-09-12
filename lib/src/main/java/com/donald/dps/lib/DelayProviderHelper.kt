package com.donald.dps.lib

import android.os.Build

object DelayProviderHelper {
    @JvmStatic
    fun enableDelayProvider(delayMS: Long? = 300) {
        //低版本中，保持原始启动流程。高版本代理模式，才支持延迟启动。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            //须做版本判断，因为低版本中没有AppComponentFactory
            DelayProviderFactory.enableProvider(delayMS)
        }
    }

    /**
     * 取消延迟启动，需要在Application.attachBaseContext中调用，加入需要取消延迟启动的名称
     * */
    @JvmStatic
    fun cancelDelayProvider(name: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            DelayProviderFactory.derectProviderNames.add(name)
        }
    }

    /**
     * 是否延迟启动Provider，False则取消延迟启动，默认延迟启动
     * */
    @JvmStatic
    fun delayProvider(delay: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            DelayProviderFactory.derectProvider = !delay
        }
    }
}