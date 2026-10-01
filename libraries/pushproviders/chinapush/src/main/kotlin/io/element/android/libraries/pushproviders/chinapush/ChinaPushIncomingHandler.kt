/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.push.api.push.FetchPushForegroundServiceManager
import io.element.android.libraries.pushproviders.api.PushHandler

interface ChinaPushIncomingHandler {
    suspend fun handle(
        provider: String,
        payload: Map<String, String?>,
        highPriority: Boolean = true,
    ): Boolean
}

@ContributesBinding(AppScope::class)
class DefaultChinaPushIncomingHandler(
    private val parser: ChinaPushParser,
    private val pushHandler: PushHandler,
    private val fetchPushForegroundServiceManager: FetchPushForegroundServiceManager,
) : ChinaPushIncomingHandler {
    override suspend fun handle(
        provider: String,
        payload: Map<String, String?>,
        highPriority: Boolean,
    ): Boolean {
        if (highPriority) {
            fetchPushForegroundServiceManager.start()
        }

        val pushData = parser.parse(payload)
        if (pushData == null) {
            pushHandler.handleInvalid(
                providerInfo = provider,
                data = payload.entries.joinToString("\n") { "${it.key}: ${it.value}" },
            )
            if (highPriority) {
                fetchPushForegroundServiceManager.stop()
            }
            return false
        }

        val handled = pushHandler.handle(
            pushData = pushData,
            providerInfo = provider,
        )
        if (!handled && highPriority) {
            fetchPushForegroundServiceManager.stop()
        }
        return handled
    }
}
