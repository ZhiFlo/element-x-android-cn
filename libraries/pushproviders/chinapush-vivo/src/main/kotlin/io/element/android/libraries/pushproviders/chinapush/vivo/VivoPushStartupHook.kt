/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush.vivo

import android.app.Activity
import android.os.Build
import com.vivo.push.PushClient
import com.vivo.push.PushConfig
import com.vivo.push.listener.IPushQueryActionListener
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import io.element.android.features.enterprise.api.AppStartupHook
import io.element.android.libraries.di.annotations.AppCoroutineScope
import io.element.android.libraries.pushproviders.chinapush.ChinaPushTokenHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@ContributesIntoSet(AppScope::class)
class VivoPushStartupHook(
    private val tokenHandler: ChinaPushTokenHandler,
    @AppCoroutineScope private val coroutineScope: CoroutineScope,
) : AppStartupHook {
    override suspend fun onAppStartup(activity: Activity) {
        if (BuildConfig.VIVO_APP_ID.isBlank() || BuildConfig.VIVO_API_KEY.isBlank()) return
        if (!isVivoFamilyDevice()) return

        val application = activity.application
        runCatching {
            val client = PushClient.getInstance(application)
            client.initialize(
                PushConfig.Builder()
                    .agreePrivacyStatement(true)
                    .build(),
            )
            client.turnOnPush { state ->
                if (state == 0) {
                    queryToken(client)
                } else {
                    Timber.w("vivo Push turnOnPush failed: %s", state)
                }
            }
        }.onFailure {
            Timber.w(it, "Unable to initialize vivo Push")
        }
    }

    private fun queryToken(client: PushClient) {
        client.getRegId(object : IPushQueryActionListener {
            override fun onSuccess(regId: String?) {
                if (regId.isNullOrBlank()) return
                coroutineScope.launch {
                    tokenHandler.handle(PROVIDER, regId)
                }
            }

            override fun onFail(state: Int) {
                Timber.w("vivo Push token query failed: %s", state)
            }
        })
    }

    private fun isVivoFamilyDevice(): Boolean {
        return sequenceOf(Build.MANUFACTURER, Build.BRAND)
            .filterNotNull()
            .map(String::lowercase)
            .any { value -> value.contains("vivo") || value.contains("iqoo") }
    }

    companion object {
        const val PROVIDER = "vivo"
    }
}
