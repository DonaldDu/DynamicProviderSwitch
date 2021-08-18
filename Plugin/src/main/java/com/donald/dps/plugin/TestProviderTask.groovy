package com.donald.dps.plugin


import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

class TestProviderTask extends DefaultTask {
    @InputFile
    File manifest
    @InputDirectory
    File apkFolder
    @Input
    String apkName

    @TaskAction
    void checkApk() throws IOException {
        TestProvider.checkNoEnabledSplitProviderInApk(manifest, apkFolder, apkName)
    }
}