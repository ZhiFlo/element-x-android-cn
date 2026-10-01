/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush.honor

import android.app.Activity
import com.hihonor.push.sdk.HonorPushCallback
import com.hihonor.push.sdk.HonorPushClient
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import io.element.android.features.enterprise.api.AppStartupHook
import io.element.android.libraries.di.annotations.AppCoroutineScope
import io.element.android.libraries.pushproviders.chinapush.BuildConfig
import io.element.android.libraries.pushproviders.chinapush.ChinaPushTokenHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@ContributesIntoSet(AppScope::class)
class HonorPushStartupHook(
    private val tokenHandler: ChinaPushTokenHandler,
    @AppCoroutineScope private val coroutineScope: CoroutineScope,
) : AppStartupHook {
    override suspend fun onAppStartup(activity: Activity) {
        if (BuildConfig.HONOR_APP_ID.isBlank()) return

        val context = activity.applicationContext
        val client = HonorPushClient.getInstance()
        if (!client.checkSupportHonorPush(context)) return

        runCatching {
            client.init(context, false)
            client.getPushToken(object : HonorPushCallback<String?> {
                override fun onSuccess(pushToken: String?) {
                    if (pushToken.isNullOrBlank()) return
                    coroutineScope.launch {
                        tokenHandler.handle(PROVIDER, pushToken)
                    }
                }

                override fun onFailure(errorCode: Int, errorString: String) {
                    Timber.w("Honor Push token error %s: %s", errorCode, errorString)
                }
            })
        }.onFailure {
            Timber.w(it, "Unable to initialize Honor Push")
        }
    }

    companion object {
        const val PROVIDER = "honor"
    }
}
