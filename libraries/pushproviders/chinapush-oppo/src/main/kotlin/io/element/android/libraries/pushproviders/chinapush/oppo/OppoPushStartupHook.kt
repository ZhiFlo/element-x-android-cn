/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush.oppo

import android.app.Activity
import android.os.Build
import com.heytap.msp.push.HeytapPushManager
import com.heytap.msp.push.callback.ICallBackResultService
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import io.element.android.features.enterprise.api.AppStartupHook
import io.element.android.libraries.di.annotations.AppCoroutineScope
import io.element.android.libraries.pushproviders.chinapush.ChinaPushTokenHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@ContributesIntoSet(AppScope::class)
class OppoPushStartupHook(
    private val tokenHandler: ChinaPushTokenHandler,
    @AppCoroutineScope private val coroutineScope: CoroutineScope,
) : AppStartupHook {
    override suspend fun onAppStartup(activity: Activity) {
        if (BuildConfig.OPPO_APP_KEY.isBlank() || BuildConfig.OPPO_APP_SECRET.isBlank()) return
        if (!isOppoFamilyDevice()) return

        val context = activity.applicationContext
        runCatching {
            HeytapPushManager.init(context, false)
            if (!HeytapPushManager.isSupportPush(context)) return

            HeytapPushManager.getRegisterID()
                .takeIf(String::isNotBlank)
                ?.let { tokenHandler.handle(PROVIDER, it) }

            HeytapPushManager.register(
                context,
                BuildConfig.OPPO_APP_KEY,
                BuildConfig.OPPO_APP_SECRET,
                object : ICallBackResultService {
                    override fun onRegister(
                        responseCode: Int,
                        registerID: String?,
                        packageName: String?,
                        miniPackageName: String?,
                    ) {
                        if (responseCode == 0 && !registerID.isNullOrBlank()) {
                            coroutineScope.launch {
                                tokenHandler.handle(PROVIDER, registerID)
                            }
                        }
                    }

                    override fun onUnRegister(responseCode: Int, packageName: String?, miniPackageName: String?) = Unit
                    override fun onSetPushTime(responseCode: Int, message: String?) = Unit
                    override fun onGetPushStatus(responseCode: Int, status: Int) = Unit
                    override fun onGetNotificationStatus(responseCode: Int, status: Int) = Unit

                    override fun onError(
                        responseCode: Int,
                        message: String?,
                        packageName: String?,
                        miniPackageName: String?,
                    ) {
                        Timber.w("OPPO Push error %s: %s", responseCode, message)
                    }
                },
            )
        }.onFailure {
            Timber.w(it, "Unable to initialize OPPO Push")
        }
    }

    private fun isOppoFamilyDevice(): Boolean {
        return sequenceOf(Build.MANUFACTURER, Build.BRAND)
            .filterNotNull()
            .map(String::lowercase)
            .any { value ->
                value.contains("oppo") || value.contains("oneplus") || value.contains("realme")
            }
    }

    companion object {
        const val PROVIDER = "oppo"
    }
}
