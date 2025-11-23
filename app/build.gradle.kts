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
import jmdroid.androidApplication

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("jm.droid.plugin.httpmonitor")
}

androidApplication("jm.droid.sample.mockserver") {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("shrinker-rules.pro", "shrinker-rules-android.pro")
            signingConfig = signingConfigs["debug"]
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}
dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.material)
    implementation(libs.squareup.moshi.runtime)
    implementation(libs.squareup.okhttp.logging)
    implementation(libs.squareup.okhttp.runtime)
    implementation(libs.squareup.retrofit.converter.moshi)
    implementation(libs.squareup.retrofit.runtime)

//    debugImplementation("io.github.jiangjm424:view-db:0.0.4")
//    debugImplementation("io.github.jiangjm424:http-monitor:+")
    debugImplementation(project(":mock-server"))
//    debugImplementation(project(":http-monitor"))

    testImplementation(libs.bundles.test.jvm)

    androidTestImplementation(libs.bundles.test.android)
}
