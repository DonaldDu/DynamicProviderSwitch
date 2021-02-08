package com.donald.dps.plugin

import java.io.File

object TestProvider {
    @JvmStatic
    fun checkNoEnabledSplitProviderInApk(manifest: File, apkFolder: File, apkName: String) {
        val apk = apkFolder.findApk(apkName)
        if (apk?.exists() == true) {
            val providers =
                manifest.readSplitProvidersFromManifest()?.toMutableList() ?: mutableListOf()
            if (providers.isNotEmpty()) {
                val apkProviders = apk.readDisabledProvidersFromApk()
                    ?: throw IllegalStateException("read apk provider failed")
                providers.removeAll { apkProviders.contains(it.provider) }
                if (providers.isNotEmpty()) {
                    val msg = providers.joinToString("\n") { "${it.split} -> ${it.provider}" }
                    throw IllegalStateException("found enabled split provider\n$msg")
                }
            }
            println("No enabled split provider was found, Everything is ok !")
        } else {
            println("no apk was found")
        }
    }
}

fun File.findApk(apkName: String): File? {
    if (!exists()) return null
    return if (isDirectory) {
        listFiles()?.forEach {
            val apk = it.findApk(apkName)
            if (apk != null) return apk
        }
        null
    } else {
        if (name == apkName) this else null
    }
}