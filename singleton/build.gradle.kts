import jmdroid.androidLibrary

plugins {
    id("com.android.library")
    id("kotlin-android")
}
androidLibrary(name = "jm.droid.lib.singleton", config = true, publish = true) {
}

dependencies {
    testImplementation(libs.bundles.test.jvm)
    androidTestImplementation(libs.bundles.test.android)
}
