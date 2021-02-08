package com.donald.dps.plugin


import org.gradle.api.Plugin
import org.gradle.api.Project

class DynamicProviderSwitchPlugin implements Plugin<Project> {
    private def QIGSAW = "qigsaw2"
    private Project project
    private DynamicProviderSwitch providerDecorator = new DynamicProviderSwitch()

    @Override
    void apply(Project project) {
        this.project = project
        if (project.extensions.android != null) createTask()
        else project.afterEvaluate { createTask() }
    }

    private void createTask() {
        createUpdateManifestTask()
        createTestProviderTask()
    }

    private void createTestProviderTask() {
        project.extensions.android.applicationVariants.all { variant ->
            variant.outputs.all { output ->
                def manifestProcessorTask = output.getProcessManifestProvider().get()
                TestProviderTask task = project.tasks.create("testProvider${variant.name.capitalize()}", TestProviderTask)
                task.manifest = manifestProcessorTask.mainMergedManifest.get().asFile
                task.apkName = output.outputFileName
                task.apkFolder = new File(project.buildDir, "outputs/apk")
                task.setGroup(QIGSAW)
            }
        }
    }

    private void createUpdateManifestTask() {
        project.extensions.android.applicationVariants.all { variant ->
            variant.outputs.all { output ->
                def manifestProcessorTask = output.getProcessManifestProvider().get()
                ManifestTask task = project.tasks.create("qigsawProcess${variant.name.capitalize()}Manifest", ManifestTask)
                task.decorator = providerDecorator
                task.manifest = getMergedManifests(variant.name)
                task.setGroup(QIGSAW)
                manifestProcessorTask.finalizedBy task
            }
        }
    }

    private File getMergedManifests(String type) {
        // "intermediates/merged_manifests/%s/AndroidManifest.xml"
        def manifest = project.findProject('MERGED_MANIFESTS')
        if (manifest != null) {
            manifest = String.format(manifest, type)
            return new File(project.buildDir, manifest)
        } else {
            //buildDir+intermediates/merged_manifests/{debug|release}/AndroidManifest.xml//4.1.2
            return new File(project.buildDir, "intermediates/merged_manifests/$type/AndroidManifest.xml")
        }
    }
}