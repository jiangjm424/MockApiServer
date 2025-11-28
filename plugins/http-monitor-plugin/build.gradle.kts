import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.mavenPublishing
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
        create("httpMonitorPlugin") {
            id = "jm.droid.plugin.httpmonitor"
            implementationClass = "jm.droid.gradle.HttpMonitorPlugin"
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

mavenPublishing {
    pomFromGradleProperties()
    publishToMavenCentral()
    signAllPublications()
    configure(GradlePlugin(JavadocJar.Javadoc(), true))
}
