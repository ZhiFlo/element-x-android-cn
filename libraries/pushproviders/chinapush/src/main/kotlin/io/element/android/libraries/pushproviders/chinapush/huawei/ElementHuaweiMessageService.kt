/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush.huawei

import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.bindings
import io.element.android.libraries.di.annotations.AppCoroutineScope
import io.element.android.libraries.pushproviders.chinapush.ChinaPushIncomingHandler
import io.element.android.libraries.pushproviders.chinapush.ChinaPushTokenHandler
import io.element.android.libraries.pushproviders.chinapush.toChinaPushPayloadMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ElementHuaweiMessageService : HmsMessageService() {
    @Inject lateinit var tokenHandler: ChinaPushTokenHandler
    @Inject lateinit var incomingHandler: ChinaPushIncomingHandler
    @AppCoroutineScope
    @Inject lateinit var coroutineScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        bindings<HuaweiMessageServiceBindings>().inject(this)
    }

    override fun onNewToken(token: String?) {
        if (token.isNullOrBlank()) return
        coroutineScope.launch {
            tokenHandler.handle(HuaweiPushStartupHook.PROVIDER, token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        val safeMessage = message ?: return
        val payload = safeMessage.dataOfMap
            .takeIf { it.isNotEmpty() }
            ?.mapValues { it.value }
            ?: safeMessage.data.toChinaPushPayloadMap()
        if (payload.isEmpty()) return
        coroutineScope.launch {
            incomingHandler.handle(
                provider = HuaweiPushStartupHook.PROVIDER,
                payload = payload,
            )
        }
    }
}
