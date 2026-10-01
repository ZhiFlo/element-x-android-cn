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

val oppoAppKey = providers.gradleProperty("chinapush.oppo.appKey")
    .orElse(providers.environmentVariable("CHINA_PUSH_OPPO_APP_KEY"))
    .getOrElse("")
val oppoAppSecret = providers.gradleProperty("chinapush.oppo.appSecret")
    .orElse(providers.environmentVariable("CHINA_PUSH_OPPO_APP_SECRET"))
    .getOrElse("")
val oppoSdkAar = providers.gradleProperty("chinapush.oppo.aar")
    .orElse(providers.environmentVariable("CHINA_PUSH_OPPO_AAR"))
    .getOrElse("$projectDir/libs/oppo_com.heytap.msp_V3.5.3.aar")

android {
    namespace = "io.element.android.libraries.pushproviders.chinapush.oppo"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigFieldStr("OPPO_APP_KEY", oppoAppKey)
        buildConfigFieldStr("OPPO_APP_SECRET", oppoAppSecret)
        consumerProguardFiles("consumer-rules.pro")
    }
}

setupDependencyInjection()

dependencies {
    implementation(files(oppoSdkAar))
    implementation(libs.coroutines.core)
    implementation(projects.features.enterprise.api)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.di)
    implementation(projects.libraries.pushproviders.chinapush)
}
