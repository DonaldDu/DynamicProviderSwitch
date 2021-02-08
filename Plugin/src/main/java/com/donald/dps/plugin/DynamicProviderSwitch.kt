package com.donald.dps.plugin

import net.dongliu.apk.parser.ApkFile
import org.dom4j.Document
import org.dom4j.Element
import org.dom4j.QName
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class DynamicProviderSwitch {
    private fun File.writeXML(doc: Document) {
        val writer = XMLWriter(OutputStreamWriter(FileOutputStream(this)))
        writer.write(doc)
        writer.close()
    }

    fun updateSelf(manifest: File) {
        if (!manifest.exists() || manifest.length() == 0L) return
        var update = false
        val saxReader = SAXReader()
        val document = saxReader.read(manifest)
        val providers = document.selectNodes("//provider") ?: return
        providers.filterIsInstance(Element::class.java).forEach {
            if (it.splitName != null && it.enabled) {
                update = true
                it.enabled = false
            }
        }
        if (update) manifest.writeXML(document)
    }

}

private val enabledQName = QName.get("android:enabled", "http://schemas.android.com/apk/res/android")
private var Element.enabled: Boolean
    get() {
        return attributeValue("enabled") != "false"
    }
    set(value) {
        val a = attribute("enabled")
        if (a != null) a.value = value.toString()
        else addAttribute(enabledQName, value.toString())
    }

private val splitQName by lazy { QName.get("tools:replace", "http://schemas.android.com/tools") }
private var Element.splitName: String?
    get() {
        val name = attributeValue("splitName")
        return if (name.isNullOrEmpty()) null else name
    }
    set(value) {
        if (value == null) remove(attribute("splitName"))
        else addAttribute(splitQName, value)
    }

fun File.readSplitProvidersFromManifest(): List<SplitProvider>? {
    if (!exists() || length() == 0L) return null
    val saxReader = SAXReader()
    val document = saxReader.read(this)
    val providers = document.selectNodes("//provider") ?: return null
    val ps = providers.filterIsInstance(Element::class.java)
        .filter { it.splitName != null && it.enabled }
    if (ps.isEmpty()) return null
    return ps.map { SplitProvider(it.splitName!!, it.attributeValue("name")) }
}

fun File.readDisabledProvidersFromApk(): List<String>? {
    val apk = ApkFile(this)
    val saxReader = SAXReader()
    val document = saxReader.read(apk.manifestXml.byteInputStream())
    apk.close()
    val providers = document.selectNodes("//provider") ?: return null
    return providers.filterIsInstance(Element::class.java)
        .filter { !it.enabled }
        .map { it.attributeValue("name") }
}

data class SplitProvider(val split: String, val provider: String)