package com.donald.dps.plugin

import com.android.SdkConstants
import com.android.build.gradle.tasks.ProcessApplicationManifest
import com.android.build.gradle.tasks.ProcessMultiApkApplicationManifest
import com.dhy.openusage.OpenUsage
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class DynamicProviderSwitchPlugin implements Plugin<Project> {
    private Project project

    @Override
    void apply(Project project) {
        this.project = project
        if (project.hasProperty('android')) createTask()
        else project.afterEvaluate { createTask() }
        initOpenUsage()
    }

    private void createTask() {
        createUpdateManifestTask()
        createTestProviderTask()
    }

    private void createTestProviderTask() {
        project.extensions.android.applicationVariants.all { variant ->
            variant.outputs.all { output ->
                def manifestProcessorTask = output.getProcessManifestProvider().get()
                TestProviderTask task = project.tasks.create("checkDisabled${variant.name.capitalize()}Provider", TestProviderTask)
                task.manifest = manifestProcessorTask.mainMergedManifest.get().asFile
                task.apkName = output.outputFileName
                task.apkFolder = new File(project.buildDir, "outputs/apk")
                task.setGroup('verification')
            }
        }
    }

    private void createUpdateManifestTask() {
        project.extensions.android.applicationVariants.all { variant ->
            variant.outputs.all { output ->
                def manifestProcessorTask = output.getProcessManifestProvider().get()
                ManifestTask task = project.tasks.create("processDisable${variant.name.capitalize()}Provider", ManifestTask)
                task.manifest = getMergedManifests(manifestProcessorTask)
                manifestProcessorTask.finalizedBy task
            }
        }
    }

    private static File getMergedManifests(manifestProcessorTask) {
        if (manifestProcessorTask instanceof ProcessMultiApkApplicationManifest) {//'com.android.tools.build:gradle:>4.1.0'
            def task = manifestProcessorTask as ProcessMultiApkApplicationManifest
            def folder = task.multiApkManifestOutputDirectory.get().asFile
            return new File(folder, SdkConstants.ANDROID_MANIFEST_XML)
        } else if (manifestProcessorTask instanceof ProcessApplicationManifest) {//'com.android.tools.build:gradle:3.5.4'
            def folder = manifestProcessorTask.manifestOutputDirectory.get().asFile
            return new File(folder, SdkConstants.ANDROID_MANIFEST_XML)
        } else throw new GradleException("Can't get 'MergedManifestFile'")
    }

    private void initOpenUsage() {
        project.afterEvaluate {
            def name = 'DynamicProviderSwitch'
            def url = 'https://github.com/DonaldDu/DynamicProviderSwitch'
            OpenUsage.report(project, name, url)
        }
    }
}