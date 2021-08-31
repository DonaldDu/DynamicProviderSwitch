package com.donald.dps.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

class ManifestTask extends DefaultTask {
    @InputFile
    File manifest

    @TaskAction
    void generate() throws IOException {
        if (manifest != null) DynamicProviderSwitch.updateSelf(manifest)
    }
}