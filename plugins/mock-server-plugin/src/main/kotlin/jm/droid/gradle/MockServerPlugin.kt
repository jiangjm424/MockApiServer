package jm.droid.gradle

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import jm.droid.gradle.transform.MockServerClassVisitorFactory
import org.gradle.api.Plugin
import org.gradle.api.Project

class MockServerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Only apply plugin for Android application modules, not library modules
        // Check if the project has applied the 'com.android.application' plugin
        val hasApplicationPlugin = project.plugins.hasPlugin("com.android.application")
        if (!hasApplicationPlugin) {
            // Not an application module, skip plugin functionality
            return
        }
        // Automatically add httpmonitor dependency only for debug variant
        project.dependencies.add("debugImplementation", "io.github.jiangjm424:mock-server-api:+")

        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

        androidComponents.onVariants { variant ->
            // Only apply plugin for debug variants
            if (variant.buildType == "debug") {
                variant.instrumentation.transformClassesWith(
                    MockServerClassVisitorFactory::class.java,
                    InstrumentationScope.ALL
                ) { }

                variant.instrumentation.setAsmFramesComputationMode(
                    FramesComputationMode.COPY_FRAMES
                )
            }
        }
    }
}






