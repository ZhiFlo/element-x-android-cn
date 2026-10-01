/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.core.extensions.flatMap
import io.element.android.libraries.matrix.api.MatrixClientProvider
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.pushproviders.api.PusherSubscriber
import io.element.android.libraries.pushstore.api.UserPushStoreFactory
import io.element.android.libraries.sessionstorage.api.SessionStore
import io.element.android.libraries.sessionstorage.api.toUserList
import timber.log.Timber

interface ChinaPushTokenHandler {
    suspend fun handle(provider: String, token: String)
}

@ContributesBinding(AppScope::class)
class DefaultChinaPushTokenHandler(
    private val store: ChinaPushStore,
    private val pusherSubscriber: PusherSubscriber,
    private val sessionStore: SessionStore,
    private val userPushStoreFactory: UserPushStoreFactory,
    private val matrixClientProvider: MatrixClientProvider,
) : ChinaPushTokenHandler {
    override suspend fun handle(provider: String, token: String) {
        val normalizedProvider = provider.lowercase()
        require(ChinaPushConfig.providers.any { it.id == normalizedProvider }) {
            "Unsupported China Push provider: $provider"
        }
        store.storeToken(normalizedProvider, token)

        sessionStore.getAllSessions().toUserList()
            .map { SessionId(it) }
            .forEach { sessionId ->
                val userStore = userPushStoreFactory.getOrCreate(sessionId)
                val isUsingChinaPush = userStore.getPushProviderName() == ChinaPushConfig.NAME
                val isUsingProvider = store.getSelectedProvider() == normalizedProvider
                if (!isUsingChinaPush || !isUsingProvider) return@forEach

                matrixClientProvider
                    .getOrRestore(sessionId)
                    .onFailure {
                        Timber.e(it, "Failed to restore session $sessionId for China Push token update")
                    }
                    .flatMap { client ->
                        pusherSubscriber.registerPusher(
                            matrixClient = client,
                            pushKey = "$normalizedProvider:$token",
                            gateway = ChinaPushConfig.PUSHER_HTTP_URL,
                        )
                    }
                    .onFailure {
                        Timber.e(it, "Failed to re-register China Push pusher for $sessionId")
                    }
            }
    }
}
