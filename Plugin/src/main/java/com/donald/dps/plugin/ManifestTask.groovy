package com.donald.dps.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class ManifestTask extends DefaultTask {
    @Input
    DynamicProviderSwitch decorator
    @Input
    File manifest

    @TaskAction
    void generate() throws IOException {
        if (decorator != null && manifest != null) decorator.updateSelf(manifest)
    }
}