/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush

import com.google.common.truth.Truth.assertThat
import io.element.android.libraries.matrix.test.FakeMatrixClient
import io.element.android.libraries.push.test.FakePusherSubscriber
import io.element.android.libraries.pushproviders.api.Distributor
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ChinaPushProviderTest {
    @Test
    fun `registerWith sends provider-prefixed token to Matrix pusher`() = runTest {
        val store = FakeChinaPushStore(
            tokens = mutableMapOf("xiaomi" to "vendor-token"),
        )
        var capturedPushKey: String? = null
        var capturedGateway: String? = null
        val provider = ChinaPushProvider(
            store = store,
            pusherSubscriber = FakePusherSubscriber(
                registerPusherResult = { _, pushKey, gateway ->
                    capturedPushKey = pushKey
                    capturedGateway = gateway
                    Result.success(Unit)
                },
            ),
        )

        val result = provider.registerWith(
            matrixClient = FakeMatrixClient(),
            distributor = Distributor("xiaomi", "Xiaomi Push"),
        )

        assertThat(result.isSuccess).isTrue()
        assertThat(capturedPushKey).isEqualTo("xiaomi:vendor-token")
        assertThat(capturedGateway).isEqualTo(ChinaPushConfig.PUSHER_HTTP_URL)
        assertThat(store.selectedProviderValue).isEqualTo("xiaomi")
    }

    private class FakeChinaPushStore(
        private val tokens: MutableMap<String, String?> = mutableMapOf(),
        var selectedProviderValue: String? = null,
    ) : ChinaPushStore {
        override fun getToken(provider: String): String? = tokens[provider]

        override fun storeToken(provider: String, token: String?) {
            tokens[provider] = token
        }

        override fun getSelectedProvider(): String? = selectedProviderValue

        override fun setSelectedProvider(provider: String?) {
            selectedProviderValue = provider
        }

        override fun getKnownProviders(): List<String> = tokens.filterValues { !it.isNullOrBlank() }.keys.toList()
    }
}
