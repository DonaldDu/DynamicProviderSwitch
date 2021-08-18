package com.donald.dps.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

class ManifestTask extends DefaultTask {
    @Internal
    DynamicProviderSwitch decorator
    @InputFile
    File manifest

    @TaskAction
    void generate() throws IOException {
        if (decorator != null && manifest != null) decorator.updateSelf(manifest)
    }
}