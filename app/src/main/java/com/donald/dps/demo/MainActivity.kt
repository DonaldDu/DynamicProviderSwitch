package com.donald.dps.demo

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ComponentName
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("MainActivity", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btEnabled.setOnClickListener {
            ComponentName(this, "com.donald.dps.demo.NotFoundProvider1").enabled = true
            ComponentName(this, "com.donald.dps.demo.NotFoundProvider2").enabled = true
            ComponentName(this, "com.donald.dps.demo.NotFoundProvider3").enabled = true
            clearApplicationUserData()
        }
        btCrash.setOnClickListener {
            println(1 / 0)
        }

        btStartProvider.setOnClickListener {
            val uri = Uri.parse("content://${packageName}.a")
            contentResolver.insert(uri, null)
        }
        btStartProvider1.setOnClickListener {
            val NotFoundProvider1 = "${packageName}.NotFoundProvider1"
            val p = ContentProviderProxy.providers.find { it.realContentProviderClassName == NotFoundProvider1 }!!
            p.realContentProviderClassName = EmptyProvider::class.java.name

            val uri = Uri.parse("content://${packageName}.a1")
            contentResolver.insert(uri, null)
        }
        btInitProvider.setOnClickListener {
            HookUtil.providers.removeAll(disabledProviderFilter)
            HookUtil.initProvider(this)
        }
    }

    private val disabledProviderFilter: (ProviderInfo) -> Boolean = {
        Log.i(TAG, "ProviderFilter filter -> ${it.name}")
        if (it.authority != null) {
            if (!it.enabled) {
                Log.i(TAG, "ProviderFilter disabled-> ${it.name}")
                false
            } else {
                try {
                    Class.forName(it.name).name != it.name
                } catch (e: ClassNotFoundException) {
                    Log.i(TAG, "ProviderFilter ClassNotFoundException-> ${it.name}")
                    true
                }
            }
        } else {
            Log.i(TAG, "ProviderFilter null authority -> ${it.name}")
            false
        }
    }

    @SuppressLint("PrivateApi")
    fun clearApplicationUserData() {
        try {
            val observer = Class.forName("android.content.pm.IPackageDataObserver")
            val clearApp = ActivityManager::class.java.getMethod("clearApplicationUserData", String::class.java, observer)
            val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            clearApp.invoke(am, packageName, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var ComponentName.enabled: Boolean
        get() = false
        set(value) {
            val newState = if (value) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            packageManager.setComponentEnabledSetting(this, newState, PackageManager.DONT_KILL_APP)
        }
}

const val TAG = "ProviderSwitch"