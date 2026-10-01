/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.pushproviders.api.Config
import io.element.android.libraries.pushproviders.api.Distributor
import io.element.android.libraries.pushproviders.api.PushProvider
import io.element.android.libraries.pushproviders.api.PusherSubscriber

@ContributesIntoSet(AppScope::class)
class ChinaPushProvider(
    private val store: ChinaPushStore,
    private val pusherSubscriber: PusherSubscriber,
) : PushProvider {
    override val index = ChinaPushConfig.INDEX
    override val name = ChinaPushConfig.NAME
    override val supportMultipleDistributors = true

    override fun getDistributors(): List<Distributor> {
        val knownProviders = store.getKnownProviders().toSet()
        return ChinaPushConfig.providers
            .filter { it.id in knownProviders }
            .map { Distributor(value = it.id, name = it.displayName) }
    }

    override suspend fun registerWith(matrixClient: MatrixClient, distributor: Distributor): Result<Unit> {
        val token = store.getToken(distributor.value)
            ?: return Result.failure(IllegalStateException("No token available for ${distributor.value}."))
        val result = pusherSubscriber.registerPusher(
            matrixClient = matrixClient,
            pushKey = toPushKey(distributor.value, token),
            gateway = ChinaPushConfig.PUSHER_HTTP_URL,
        )
        if (result.isSuccess) {
            store.setSelectedProvider(distributor.value)
        }
        return result
    }

    override suspend fun getCurrentDistributorValue(sessionId: SessionId): String? {
        return store.getSelectedProvider()
    }

    override suspend fun getCurrentDistributor(sessionId: SessionId): Distributor? {
        val providerId = store.getSelectedProvider() ?: return null
        val provider = ChinaPushConfig.providers.firstOrNull { it.id == providerId } ?: return null
        if (store.getToken(providerId).isNullOrBlank()) return null
        return Distributor(value = provider.id, name = provider.displayName)
    }

    override suspend fun unregister(matrixClient: MatrixClient): Result<Unit> {
        val provider = store.getSelectedProvider() ?: return Result.success(Unit)
        val token = store.getToken(provider) ?: return Result.success(Unit)
        return pusherSubscriber.unregisterPusher(
            matrixClient = matrixClient,
            pushKey = toPushKey(provider, token),
            gateway = ChinaPushConfig.PUSHER_HTTP_URL,
        )
    }

    override suspend fun onSessionDeleted(sessionId: SessionId) = Unit

    override suspend fun getPushConfig(sessionId: SessionId): Config? {
        val provider = store.getSelectedProvider() ?: return null
        val token = store.getToken(provider) ?: return null
        return Config(
            url = ChinaPushConfig.PUSHER_HTTP_URL,
            pushKey = toPushKey(provider, token),
        )
    }

    override fun canRotateToken(): Boolean = false

    override suspend fun rotateToken(): Result<Unit> {
        return Result.failure(UnsupportedOperationException("Vendor push tokens are rotated by the vendor SDK."))
    }

    private fun toPushKey(provider: String, token: String): String = "$provider:$token"
}
