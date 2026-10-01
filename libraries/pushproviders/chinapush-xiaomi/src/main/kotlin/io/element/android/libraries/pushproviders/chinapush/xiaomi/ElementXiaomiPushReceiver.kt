/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush.xiaomi

import android.content.Context
import com.xiaomi.mipush.sdk.MiPushClient
import com.xiaomi.mipush.sdk.MiPushCommandMessage
import com.xiaomi.mipush.sdk.MiPushMessage
import com.xiaomi.mipush.sdk.PushMessageReceiver
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.bindings
import io.element.android.libraries.di.annotations.AppCoroutineScope
import io.element.android.libraries.pushproviders.chinapush.ChinaPushIncomingHandler
import io.element.android.libraries.pushproviders.chinapush.ChinaPushTokenHandler
import io.element.android.libraries.pushproviders.chinapush.toChinaPushPayloadMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ElementXiaomiPushReceiver : PushMessageReceiver() {
    @Inject lateinit var tokenHandler: ChinaPushTokenHandler
    @Inject lateinit var incomingHandler: ChinaPushIncomingHandler
    @AppCoroutineScope
    @Inject lateinit var coroutineScope: CoroutineScope

    override fun onReceiveRegisterResult(context: Context, message: MiPushCommandMessage) {
        if (message.command != MiPushClient.COMMAND_REGISTER || message.resultCode != 0L) {
            return
        }
        val token = message.commandArguments?.firstOrNull().orEmpty()
        if (token.isBlank()) return
        ensureInjected(context)
        coroutineScope.launch {
            tokenHandler.handle(XiaomiPushStartupHook.PROVIDER, token)
        }
    }

    override fun onReceivePassThroughMessage(context: Context, message: MiPushMessage) {
        val payload = message.content.toChinaPushPayloadMap()
        if (payload.isEmpty()) return
        ensureInjected(context)
        coroutineScope.launch {
            incomingHandler.handle(
                provider = XiaomiPushStartupHook.PROVIDER,
                payload = payload,
            )
        }
    }

    private fun ensureInjected(context: Context) {
        if (!::tokenHandler.isInitialized) {
            context.bindings<XiaomiPushReceiverBindings>().inject(this)
        }
    }
}
