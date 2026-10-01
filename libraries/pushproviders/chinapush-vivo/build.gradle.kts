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

val vivoAppId = providers.gradleProperty("chinapush.vivo.appId")
    .orElse(providers.environmentVariable("CHINA_PUSH_VIVO_APP_ID"))
    .getOrElse("")
val vivoApiKey = providers.gradleProperty("chinapush.vivo.apiKey")
    .orElse(providers.environmentVariable("CHINA_PUSH_VIVO_API_KEY"))
    .getOrElse("")
val vivoSdkAar = providers.gradleProperty("chinapush.vivo.aar")
    .orElse(providers.environmentVariable("CHINA_PUSH_VIVO_AAR"))
    .getOrElse("$projectDir/libs/vpush_clientSDK_v4.0.6.0_506.aar")

android {
    namespace = "io.element.android.libraries.pushproviders.chinapush.vivo"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigFieldStr("VIVO_APP_ID", vivoAppId)
        buildConfigFieldStr("VIVO_API_KEY", vivoApiKey)
        manifestPlaceholders["chinaPushVivoAppId"] = vivoAppId
        manifestPlaceholders["chinaPushVivoApiKey"] = vivoApiKey
        consumerProguardFiles("consumer-rules.pro")
    }
}

setupDependencyInjection()

dependencies {
    implementation(files(vivoSdkAar))
    implementation(libs.coroutines.core)
    implementation(projects.features.enterprise.api)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.di)
    implementation(projects.libraries.pushproviders.chinapush)
}
