package com.donald.dps.plugin


import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class TestProviderTask extends DefaultTask {
    @Input
    File manifest
    @Input
    File apkFolder
    @Input
    String apkName

    @TaskAction
    void checkApk() throws IOException {
        TestProvider.checkNoEnabledSplitProviderInApk(manifest, apkFolder, apkName)
    }
}