/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush.honor

import com.hihonor.push.sdk.HonorMessageService
import com.hihonor.push.sdk.HonorPushDataMsg
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.bindings
import io.element.android.libraries.di.annotations.AppCoroutineScope
import io.element.android.libraries.pushproviders.chinapush.ChinaPushIncomingHandler
import io.element.android.libraries.pushproviders.chinapush.ChinaPushTokenHandler
import io.element.android.libraries.pushproviders.chinapush.toChinaPushPayloadMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ElementHonorMessageService : HonorMessageService() {
    @Inject lateinit var tokenHandler: ChinaPushTokenHandler
    @Inject lateinit var incomingHandler: ChinaPushIncomingHandler
    @AppCoroutineScope
    @Inject lateinit var coroutineScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        bindings<HonorMessageServiceBindings>().inject(this)
    }

    override fun onNewToken(token: String?) {
        if (token.isNullOrBlank()) return
        coroutineScope.launch {
            tokenHandler.handle(HonorPushStartupHook.PROVIDER, token)
        }
    }

    override fun onMessageReceived(message: HonorPushDataMsg?) {
        val payload = message?.data.toChinaPushPayloadMap()
        if (payload.isEmpty()) return
        coroutineScope.launch {
            incomingHandler.handle(
                provider = HonorPushStartupHook.PROVIDER,
                payload = payload,
            )
        }
    }
}
