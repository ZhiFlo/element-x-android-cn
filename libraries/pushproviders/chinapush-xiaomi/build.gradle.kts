/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

import extension.buildConfigFieldStr
import extension.setupDependencyInjection

plugins {
    id("io.element.android-library")
}

val xiaomiAppId = providers.gradleProperty("chinapush.xiaomi.appId")
    .orElse(providers.environmentVariable("CHINA_PUSH_XIAOMI_APP_ID"))
    .getOrElse("")
val xiaomiAppKey = providers.gradleProperty("chinapush.xiaomi.appKey")
    .orElse(providers.environmentVariable("CHINA_PUSH_XIAOMI_APP_KEY"))
    .getOrElse("")
val xiaomiSdkCoordinate = providers.gradleProperty("chinapush.xiaomi.mavenCoordinate").orNull
val xiaomiSdkAar = providers.gradleProperty("chinapush.xiaomi.aar")
    .orElse(providers.environmentVariable("CHINA_PUSH_XIAOMI_AAR"))
    .getOrElse("$projectDir/libs/MiPush_SDK_Client_6_0_1-C_3rd.aar")

android {
    namespace = "io.element.android.libraries.pushproviders.chinapush.xiaomi"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigFieldStr("XIAOMI_APP_ID", xiaomiAppId)
        buildConfigFieldStr("XIAOMI_APP_KEY", xiaomiAppKey)
        consumerProguardFiles("consumer-rules.pro")
    }
}

setupDependencyInjection()

dependencies {
    if (xiaomiSdkCoordinate != null) {
        implementation(xiaomiSdkCoordinate)
    } else {
        implementation(files(xiaomiSdkAar))
    }

    implementation(libs.coroutines.core)
    implementation(projects.features.enterprise.api)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.di)
    implementation(projects.libraries.pushproviders.chinapush)
}
