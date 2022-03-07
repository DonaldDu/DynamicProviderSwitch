package com.donald.dps.demo

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.net.Uri
import android.os.Build
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
            providers().forEach {
                ComponentName(this, it.name).enabled = true
            }
//            clearApplicationUserData()
        }
        btCrash.setOnClickListener {
            println(Class.forName("abc").name)
        }

        btStartProvider.setOnClickListener {
            val uri = Uri.parse("content://${packageName}.a")
            contentResolver.insert(uri, null)
        }

        btStartProvider1.setOnClickListener {
            try {
                val uri = Uri.parse("content://${packageName}.a1")
                contentResolver.insert(uri, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        btClear.setOnClickListener {
            clearApplicationUserData()
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

    private fun Context.providers(): MutableList<ProviderInfo> {
        @Suppress("DEPRECATION")
        val disabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) PackageManager.MATCH_DISABLED_COMPONENTS else PackageManager.GET_DISABLED_COMPONENTS
        val info = packageManager.getPackageInfo(packageName, PackageManager.GET_PROVIDERS or disabled)
        val ps = info.providers?.toList() ?: emptyList()
        return ps.toMutableList()
    }
}

const val TAG = "ProviderSwitch"