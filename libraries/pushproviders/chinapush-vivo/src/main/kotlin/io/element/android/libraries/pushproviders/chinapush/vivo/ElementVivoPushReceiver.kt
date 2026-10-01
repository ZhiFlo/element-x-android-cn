/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush.vivo

import android.content.Context
import com.vivo.push.model.UnvarnishedMessage
import com.vivo.push.sdk.OpenClientPushMessageReceiver
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.bindings
import io.element.android.libraries.di.annotations.AppCoroutineScope
import io.element.android.libraries.pushproviders.chinapush.ChinaPushIncomingHandler
import io.element.android.libraries.pushproviders.chinapush.ChinaPushTokenHandler
import io.element.android.libraries.pushproviders.chinapush.toChinaPushPayloadMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ElementVivoPushReceiver : OpenClientPushMessageReceiver() {
    @Inject lateinit var tokenHandler: ChinaPushTokenHandler
    @Inject lateinit var incomingHandler: ChinaPushIncomingHandler
    @AppCoroutineScope
    @Inject lateinit var coroutineScope: CoroutineScope

    override fun onReceiveRegId(context: Context?, regId: String?) {
        val safeContext = context ?: return
        if (regId.isNullOrBlank()) return
        ensureInjected(safeContext)
        coroutineScope.launch {
            tokenHandler.handle(VivoPushStartupHook.PROVIDER, regId)
        }
    }

    override fun onTransmissionMessage(context: Context?, message: UnvarnishedMessage?) {
        val safeContext = context ?: return
        val payload = message?.message.toChinaPushPayloadMap()
        if (payload.isEmpty()) return
        ensureInjected(safeContext)
        coroutineScope.launch {
            incomingHandler.handle(
                provider = VivoPushStartupHook.PROVIDER,
                payload = payload,
            )
        }
    }

    private fun ensureInjected(context: Context) {
        if (!::tokenHandler.isInitialized) {
            context.bindings<VivoPushReceiverBindings>().inject(this)
        }
    }
}
