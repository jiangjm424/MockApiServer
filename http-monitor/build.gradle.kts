import jmdroid.androidLibrary

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.google.devtools.ksp")
}

androidLibrary(name = "jm.droid.lib.httpmonitor", config = true, publish = true) {
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.okhttp)
    implementation(libs.androidx.room.ktx)
    implementation(libs.material)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.jmdroid.adapter)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.bundles.test.jvm)
    androidTestImplementation(libs.bundles.test.android)
}
