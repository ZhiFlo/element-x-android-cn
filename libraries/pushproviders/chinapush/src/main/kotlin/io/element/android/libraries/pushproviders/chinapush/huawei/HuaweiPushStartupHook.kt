/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush.huawei

import android.app.Activity
import android.os.Build
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.push.HmsMessaging
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import io.element.android.features.enterprise.api.AppStartupHook
import io.element.android.libraries.pushproviders.chinapush.BuildConfig
import io.element.android.libraries.pushproviders.chinapush.ChinaPushTokenHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@ContributesIntoSet(AppScope::class)
class HuaweiPushStartupHook(
    private val tokenHandler: ChinaPushTokenHandler,
) : AppStartupHook {
    override suspend fun onAppStartup(activity: Activity) {
        val appId = BuildConfig.HUAWEI_APP_ID
        if (appId.isBlank() || !Build.MANUFACTURER.equals("HUAWEI", ignoreCase = true)) {
            return
        }

        val context = activity.applicationContext
        runCatching {
            HmsMessaging.getInstance(context).isAutoInitEnabled = true
            HmsMessaging.getInstance(context).turnOnPush()
            withContext(Dispatchers.IO) {
                HmsInstanceId.getInstance(context).getToken(appId, HUAWEI_TOKEN_SCOPE)
            }
        }.onSuccess { token ->
            if (token.isNotBlank()) {
                tokenHandler.handle(PROVIDER, token)
            }
        }.onFailure {
            Timber.w(it, "Unable to obtain Huawei Push token")
        }
    }

    companion object {
        const val PROVIDER = "hms"
        private const val HUAWEI_TOKEN_SCOPE = "HCM"
    }
}
