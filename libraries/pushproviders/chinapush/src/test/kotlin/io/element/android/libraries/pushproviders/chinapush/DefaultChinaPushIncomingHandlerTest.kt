/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush

import com.google.common.truth.Truth.assertThat
import io.element.android.libraries.push.test.push.FakeFetchPushForegroundServiceManager
import io.element.android.libraries.push.test.test.FakePushHandler
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultChinaPushIncomingHandlerTest {
    @Test
    fun `invalid payload redacts client secret before diagnostics`() = runTest {
        var invalidProvider: String? = null
        var invalidData: String? = null
        var started = 0
        var stopped = 0
        val handler = DefaultChinaPushIncomingHandler(
            parser = ChinaPushParser(),
            pushHandler = FakePushHandler(
                handleInvalidResult = { provider, data ->
                    invalidProvider = provider
                    invalidData = data
                },
            ),
            fetchPushForegroundServiceManager = FakeFetchPushForegroundServiceManager(
                lock = {
                    started++
                    true
                },
                unlock = {
                    stopped++
                    true
                },
            ),
        )

        val result = handler.handle(
            provider = "xiaomi",
            payload = mapOf(
                "room_id" to "!room:example.org",
                "cs" to "super-secret-value",
            ),
        )

        assertThat(result).isFalse()
        assertThat(invalidProvider).isEqualTo("xiaomi")
        assertThat(invalidData).contains("cs: <redacted>")
        assertThat(invalidData).doesNotContain("super-secret-value")
        assertThat(started).isEqualTo(1)
        assertThat(stopped).isEqualTo(1)
    }
}
