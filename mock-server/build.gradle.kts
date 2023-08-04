import jm.compile.setupLibraryModule

plugins {
    id("com.android.library")
    id("kotlin-android")
}

setupLibraryModule(name = "jm.droid.lib.server.server", publish = true, document = false)

dependencies {
    implementation(libs.apache.commons.io)
    implementation(libs.gson)
    implementation(libs.squareup.okhttp.mockserver)
    implementation(libs.squareup.okhttp.tls)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
}
