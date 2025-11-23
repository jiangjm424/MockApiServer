import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish.base)
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gradlePlugin.kotlin)
    implementation(libs.gradlePlugin.android)
    // ASM for bytecode manipulation
    implementation(libs.asm.core)
    implementation(libs.asm.commons)
}

gradlePlugin {
    plugins {
        create("mockServerPlugin") {
            id = "jm.droid.plugin.mockserver"
            implementationClass = "jm.droid.gradle.MockServerPlugin"
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions.jvmTarget = JvmTarget.JVM_17
}

// Configure Dokka
extensions.configure<DokkaExtension> {
    dokkaPublications.configureEach {
        failOnWarning.set(false)
        suppressInheritedMembers.set(true)
    }
    dokkaSourceSets.configureEach {
        jdkVersion.set(8)
        skipDeprecated.set(true)
    }
}

// Configure Maven Publishing
extensions.configure<MavenPublishBaseExtension> {
    pomFromGradleProperties()
    publishToMavenCentral()
    // Only sign if signing is configured (for Maven Central)
    // For local publishing, signing is optional
    val signingKeyId = project.findProperty("signing.keyId") as String?
    if (signingKeyId != null) {
        signAllPublications()
    }
    configure(JavaLibrary(JavadocJar.Dokka("dokkaGenerateHtml")))
}
