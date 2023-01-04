package com.donald.dps.lib

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.ProviderInfo
import android.os.Build
import java.lang.reflect.Method

/**
 * Created by zhuguohui
 * Date: 2021/9/13
 * Time: 11:23
 * https://juejin.cn/post/7007338307075964942
 */
internal object HookUtil {
    private lateinit var providers: MutableList<ProviderInfo>
    private var installContentProvidersMethod: Method? = null
    private var currentActivityThread: Any? = null

    /**
     * api>=28 -> HookProviderFactory & ContentProviderProxy,
     * else disable Provider.
     * must call in Application#attachBaseContext
     * */
    fun compatDynamicProvider(base: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            try {
                attachContext()
                disableDynamicProviders(base)
                initProvider(base)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initProvider(context: Context) {
        try {
            installContentProvidersMethod!!.invoke(currentActivityThread, context, providers)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun disableDynamicProviders(context: Context) {
        if (providers.isEmpty()) return
        val iterator = providers.iterator()
        while (iterator.hasNext()) {
            val provider = iterator.next()
            if (provider.isDisabled) {
                iterator.remove()
                ComponentName(context, provider.name).enable(context, false)
            }
        }
    }

    @SuppressLint("PrivateApi")
    @Throws(Exception::class)
    private fun attachContext() {
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val currentActivityThreadMethod = activityThreadClass.method("currentActivityThread")
        currentActivityThread = currentActivityThreadMethod.invoke(null)
        hookInstallContentProvider(activityThreadClass)
    }

    @Throws(Exception::class)
    private fun hookInstallContentProvider(activityThreadClass: Class<*>) {
        val appDataField = activityThreadClass.field("mBoundApplication")
        val appData = appDataField.get(currentActivityThread)
        val providersField = appData.javaClass.field("providers") //仅包含enabled provider
        @Suppress("UNCHECKED_CAST")
        providers = providersField.get(appData) as MutableList<ProviderInfo>
        providersField.set(appData, null)//清空provider，避免有些sdk通过provider来初始化
        installContentProvidersMethod = activityThreadClass.method("installContentProviders", Context::class, MutableList::class)
    }
}