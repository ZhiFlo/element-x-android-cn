/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush.xiaomi

import android.app.Activity
import android.os.Build
import com.xiaomi.mipush.sdk.MiPushClient
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import io.element.android.features.enterprise.api.AppStartupHook
import timber.log.Timber

@ContributesIntoSet(AppScope::class)
class XiaomiPushStartupHook : AppStartupHook {
    override suspend fun onAppStartup(activity: Activity) {
        if (BuildConfig.XIAOMI_APP_ID.isBlank() || BuildConfig.XIAOMI_APP_KEY.isBlank()) {
            return
        }
        if (!isXiaomiFamilyDevice()) {
            return
        }

        runCatching {
            MiPushClient.registerPush(
                activity.applicationContext,
                BuildConfig.XIAOMI_APP_ID,
                BuildConfig.XIAOMI_APP_KEY,
            )
        }.onFailure {
            Timber.w(it, "Unable to initialize Xiaomi Push")
        }
    }

    private fun isXiaomiFamilyDevice(): Boolean {
        return sequenceOf(Build.MANUFACTURER, Build.BRAND)
            .filterNotNull()
            .map(String::lowercase)
            .any { value ->
                value.contains("xiaomi") || value.contains("redmi") || value.contains("poco")
            }
    }

    companion object {
        const val PROVIDER = "xiaomi"
    }
}
