/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

import config.BuildTimeConfig
import extension.buildConfigFieldStr
import extension.setupDependencyInjection

plugins {
    id("io.element.android-library")
}

val huaweiPushAppId = providers.gradleProperty("chinapush.huawei.appId")
    .orElse(providers.environmentVariable("CHINA_PUSH_HUAWEI_APP_ID"))
    .getOrElse("")
val honorPushAppId = providers.gradleProperty("chinapush.honor.appId")
    .orElse(providers.environmentVariable("CHINA_PUSH_HONOR_APP_ID"))
    .getOrElse("")

android {
    namespace = "io.element.android.libraries.pushproviders.chinapush"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigFieldStr(
            name = "PUSHER_HTTP_URL",
            value = BuildTimeConfig.PUSH_CONFIG_CHINA_GATEWAY_URL,
        )
        buildConfigFieldStr(
            name = "HUAWEI_APP_ID",
            value = huaweiPushAppId,
        )
        buildConfigFieldStr(
            name = "HONOR_APP_ID",
            value = honorPushAppId,
        )
        manifestPlaceholders["chinaPushHuaweiAppId"] = huaweiPushAppId
        manifestPlaceholders["chinaPushHonorAppId"] = honorPushAppId
        consumerProguardFiles("consumer-rules.pro")
    }
}

setupDependencyInjection()

dependencies {
    implementation(libs.androidx.corektx)
    implementation(libs.coroutines.core)
    implementation("com.huawei.hms:push:6.13.0.300")
    implementation("com.hihonor.mcs:push:10.0.31.302")
    implementation(projects.features.enterprise.api)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.core)
    implementation(projects.libraries.di)
    implementation(projects.libraries.matrix.api)
    implementation(projects.libraries.push.api)
    implementation(projects.libraries.pushstore.api)
    implementation(projects.libraries.pushproviders.api)
    implementation(projects.libraries.sessionStorage.api)
}
