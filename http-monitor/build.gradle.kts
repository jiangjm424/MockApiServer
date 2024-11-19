import jm.compile.setupLibraryModule

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.google.devtools.ksp")
}

setupLibraryModule(name = "jm.droid.lib.httpmonitor")
android {
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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
