/*
 * Copyright 2023 The Jmdroid Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import jm.build.setupAppModule

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
setupAppModule(name = "jm.droid.sample.mockserver") {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

dependencies {

    debugImplementation(project(":mock-server"))

    implementation(libs.squareup.okhttp.runtime)
    implementation(libs.squareup.okhttp.logging)
    implementation(libs.squareup.retrofit.runtime)
    implementation(libs.squareup.retrofit.converter.moshi)
    implementation(libs.squareup.moshi.runtime)

    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    testImplementation(libs.bundles.test.jvm)
    androidTestImplementation(libs.bundles.test.android)
}