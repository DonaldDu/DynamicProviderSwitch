package com.donald.dps.demo

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ComponentName
import android.content.pm.PackageManager
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
//            HookUtil.initProvider(this)
            startProvider()
        }
    }

    private fun startProvider() {
        val uri = Uri.parse("content://a")
        contentResolver.insert(uri, null)
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