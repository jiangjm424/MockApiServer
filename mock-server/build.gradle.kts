import jmdroid.androidLibrary

plugins {
    id("com.android.library")
    id("kotlin-android")
}

androidLibrary(name = "jm.droid.lib.server.server", config = true, publish = true) {
}
dependencies {
    implementation(libs.apache.commons.io)
    implementation(libs.gson)
    implementation(libs.squareup.okhttp.mockserver)
    implementation(libs.squareup.okhttp.tls)
    api(libs.okio)

    testImplementation(libs.bundles.test.jvm)

    androidTestImplementation(libs.bundles.test.android)
}
