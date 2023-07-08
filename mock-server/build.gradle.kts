import jm.build.setupLibraryModule

plugins {
    id("com.android.library")
    id("kotlin-android")
}

setupLibraryModule(name = "jm.droid.lib.server.server")

dependencies {
    implementation(libs.squareup.okhttp.mockserver)
    implementation(libs.squareup.okhttp.tls)
    implementation(libs.apache.commons.io)
    implementation(libs.gson)

    testImplementation(libs.bundles.test.jvm)
    androidTestImplementation(libs.bundles.test.android)
}
