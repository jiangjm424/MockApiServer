import buildhelper.setupLibraryModule

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

setupLibraryModule(publish = true)

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.okhttp3.mockserver)
    implementation(libs.okhttp3.tls)
    implementation(libs.gson)
    implementation(libs.commons.io)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
}
