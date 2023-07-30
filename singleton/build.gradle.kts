import jm.compile.setupLibraryModule

plugins {
    id("com.android.library")
    id("kotlin-android")
}

setupLibraryModule(name = "jm.droid.lib.singleton")

dependencies {
    testImplementation(libs.bundles.test.jvm)
    androidTestImplementation(libs.bundles.test.android)
}
