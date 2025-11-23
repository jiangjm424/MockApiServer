pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// Include build for plugins
includeBuild("plugins/http-monitor-plugin")
includeBuild("plugins/mock-server-plugin")

// Public modules
include(
    "mock-server",
    "singleton",
    "http-monitor",
)

// Private modules
include(
    "app",
)
