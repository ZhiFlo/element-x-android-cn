/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush.oppo

import android.content.Context
import com.heytap.msp.push.mode.DataMessage
import com.heytap.msp.push.service.DataMessageCallbackService
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.bindings
import io.element.android.libraries.di.annotations.AppCoroutineScope
import io.element.android.libraries.pushproviders.chinapush.ChinaPushIncomingHandler
import io.element.android.libraries.pushproviders.chinapush.toChinaPushPayloadMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ElementOppoMessageService : DataMessageCallbackService() {
    @Inject lateinit var incomingHandler: ChinaPushIncomingHandler
    @AppCoroutineScope
    @Inject lateinit var coroutineScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        bindings<OppoPushServiceBindings>().inject(this)
    }

    override fun processMessage(context: Context?, message: DataMessage?) {
        super.processMessage(context, message)
        val payload = message?.content.toChinaPushPayloadMap()
            .ifEmpty { message?.dataExtra.toChinaPushPayloadMap() }
        if (payload.isEmpty()) return
        coroutineScope.launch {
            incomingHandler.handle(
                provider = OppoPushStartupHook.PROVIDER,
                payload = payload,
            )
        }
    }
}
