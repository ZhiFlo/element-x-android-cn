/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush

import com.google.common.truth.Truth.assertThat
import io.element.android.libraries.push.test.push.FakeFetchPushForegroundServiceManager
import io.element.android.libraries.push.test.test.FakePushHandler
import io.element.android.libraries.pushproviders.api.PushData
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultChinaPushIncomingHandlerTest {
    @Test
    fun `valid vendor payload is forwarded to the native push handler`() = runTest {
        var handledProvider: String? = null
        var handledPushData: PushData? = null
        var started = 0
        var stopped = 0
        val handler = DefaultChinaPushIncomingHandler(
            parser = ChinaPushParser(),
            pushHandler = FakePushHandler(
                handleResult = { pushData, provider ->
                    handledPushData = pushData
                    handledProvider = provider
                    true
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
                "kind" to "matrix",
                "event_id" to "\$event:example.org",
                "room_id" to "!room:example.org",
                "unread" to "3",
                "cs" to "client-secret",
            ),
        )

        assertThat(result).isTrue()
        assertThat(handledProvider).isEqualTo("xiaomi")
        assertThat(handledPushData?.eventId?.value).isEqualTo("\$event:example.org")
        assertThat(handledPushData?.roomId?.value).isEqualTo("!room:example.org")
        assertThat(handledPushData?.unread).isEqualTo(3)
        assertThat(handledPushData?.clientSecret).isEqualTo("client-secret")
        assertThat(started).isEqualTo(1)
        assertThat(stopped).isEqualTo(0)
    }

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
                "client_secret" to "snake-case-secret",
                "clientSecret" to "camel-case-secret",
            ),
        )

        assertThat(result).isFalse()
        assertThat(invalidProvider).isEqualTo("xiaomi")
        assertThat(invalidData).contains("cs: <redacted>")
        assertThat(invalidData).contains("client_secret: <redacted>")
        assertThat(invalidData).contains("clientSecret: <redacted>")
        assertThat(invalidData).doesNotContain("super-secret-value")
        assertThat(invalidData).doesNotContain("snake-case-secret")
        assertThat(invalidData).doesNotContain("camel-case-secret")
        assertThat(started).isEqualTo(1)
        assertThat(stopped).isEqualTo(1)
    }
}
