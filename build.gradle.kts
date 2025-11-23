import jmdroid.groupId
import jmdroid.versionName
import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessExtensionPredeclare
import kotlinx.validation.ApiValidationExtension
import kotlinx.validation.ExperimentalBCVApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

buildscript {
    repositories {
        google()
        mavenCentral()

    }
    dependencies {
        classpath(libs.gradlePlugin.android)
        classpath(libs.gradlePlugin.kotlin)
        classpath(libs.gradlePlugin.mavenPublish)
    }
}

plugins {
    alias(libs.plugins.baselineProfile) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.binaryCompatibility)
    alias(libs.plugins.spotless)
    // https://github.com/gradle/gradle/issues/20084#issuecomment-1060822638
    id("org.jetbrains.dokka")
}


extensions.configure<ApiValidationExtension> {
    nonPublicMarkers += "coil3/annotation/InternalCoilApi"
    ignoredProjects += project.subprojects.mapNotNull { project ->
//        if (project.name in publicModules) null else project.name
        null
    }
    @OptIn(ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
}

dokka {
    dokkaGeneratorIsolation = ClassLoaderIsolation()
    dokkaPublications.configureEach {
        outputDirectory.set(layout.projectDirectory.dir("docs/api"))
    }
}

dependencies {
//    for (module in setof("")) {
//        dokka(project(":$module"))
//    }
}

allprojects {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }

    // Necessary to publish to Maven.
    group = groupId
    version = versionName

    // Target JVM 8.
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
        options.compilerArgs = options.compilerArgs + "-Xlint:-options"
    }
    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions.jvmTarget = JvmTarget.JVM_1_8
    }

    // Uninstall test APKs after running instrumentation tests.
    tasks.configureEach {
        if (name == "connectedDebugAndroidTest") {
            finalizedBy("uninstallDebugAndroidTest")
        }
    }

    // https://issuetracker.google.com/issues/411739086?pli=1
    tasks.withType<AbstractTestTask>().configureEach {
        failOnNoDiscoveredTests = false
    }

    apply(plugin = "com.diffplug.spotless")

    val configureSpotless: SpotlessExtension.() -> Unit = {
        kotlin {
            target("**/*.kt", "**/*.kts")
            ktlint(libs.versions.ktlint.get()).editorConfigOverride(ktlintRules)
            endWithNewline()
            leadingTabsToSpaces()
            trimTrailingWhitespace()
        }
        format("xml") {
            target("**/*.xml")
            targetExclude("**/build/**/*.xml")
            endWithNewline()
            leadingTabsToSpaces()
            trimTrailingWhitespace()
            // Look for the first XML tag that isn't a comment (<!--) or the xml declaration (<?xml)
            licenseHeaderFile(rootProject.file("spotless/copyright.xml"), "(<[^!?])")
        }
    }

    if (project === rootProject) {
        spotless { predeclareDeps() }
        extensions.configure<SpotlessExtensionPredeclare>(configureSpotless)
    } else {
        extensions.configure(configureSpotless)
    }



    // https://youtrack.jetbrains.com/issue/CMP-5831
    //强制使用某个版本，样例代码保留
//    configurations.all {
//        resolutionStrategy.eachDependency {
//            if (requested.group == "org.jetbrains.kotlinx" && requested.name == "atomicfu") {
//                useVersion(libs.versions.atomicfu.get())
//            }
//        }
//    }
}

private val ktlintRules = buildMap {
    put("ktlint_code_style", "intellij_idea")

    put("ktlint_standard_annotation", "disabled")
    put("ktlint_standard_blank-line-before-declaration", "disabled")
    put("ktlint_standard_class-signature", "disabled")
    put("ktlint_standard_filename", "disabled")
    put("ktlint_standard_function-expression-body", "disabled")
    put("ktlint_standard_function-signature", "disabled")
    put("ktlint_standard_function-literal", "disabled")
    put("ktlint_standard_indent", "disabled")
    put("ktlint_standard_max-line-length", "disabled")
    put("ktlint_standard_no-blank-line-in-list", "disabled")
    put("ktlint_standard_no-empty-first-line-in-class-body", "disabled")
    put("ktlint_standard_spacing-between-declarations-with-annotations", "disabled")
    put("ktlint_standard_string-template-indent", "disabled")
    put("ktlint_standard_trailing-comma-on-call-site", "disabled")
    put("ktlint_standard_trailing-comma-on-declaration-site", "disabled")
    put("ktlint_standard_try-catch-finally-spacing", "disabled")

    put("ktlint_standard_backing-property-naming", "disabled")
    put("ktlint_standard_function-naming", "disabled")
    put("ktlint_standard_property-naming", "disabled")

    put("ktlint_standard_type-argument-comment", "disabled")
    put("ktlint_standard_type-parameter-comment", "disabled")
    put("ktlint_standard_value-argument-comment", "disabled")
    put("ktlint_standard_value-parameter-comment", "disabled")

    put("ktlint_standard_argument-list-wrapping", "disabled")
    put("ktlint_standard_binary-expression-wrapping", "disabled")
    put("ktlint_standard_condition-wrapping", "disabled")
    put("ktlint_standard_context-receiver-wrapping", "disabled")
    put("ktlint_standard_enum-wrapping", "disabled")
    put("ktlint_standard_if-else-wrapping", "disabled")
    put("ktlint_standard_multiline-expression-wrapping", "disabled")
    put("ktlint_standard_parameter-wrapping", "disabled")
    put("ktlint_standard_parameter-list-wrapping", "disabled")
    put("ktlint_standard_property-wrapping", "disabled")
    put("ktlint_standard_statement-wrapping", "disabled")
    put("ktlint_standard_wrapping", "disabled")
}
