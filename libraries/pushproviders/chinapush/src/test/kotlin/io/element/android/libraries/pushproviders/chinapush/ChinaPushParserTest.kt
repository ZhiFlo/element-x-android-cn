/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush

import com.google.common.truth.Truth.assertThat
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import org.junit.Test

class ChinaPushParserTest {
    private val parser = ChinaPushParser()

    @Test
    fun `valid Matrix payload is converted to PushData`() {
        val result = parser.parse(
            mapOf(
                "kind" to "matrix",
                "event_id" to "\$event:example.org",
                "room_id" to "!room:example.org",
                "unread" to "7",
                "cs" to "client-secret",
            )
        )

        assertThat(result).isNotNull()
        assertThat(result?.eventId).isEqualTo(EventId("\$event:example.org"))
        assertThat(result?.roomId).isEqualTo(RoomId("!room:example.org"))
        assertThat(result?.unread).isEqualTo(7)
        assertThat(result?.clientSecret).isEqualTo("client-secret")
    }

    @Test
    fun `missing required Matrix field is rejected`() {
        val result = parser.parse(
            mapOf(
                "room_id" to "!room:example.org",
                "cs" to "client-secret",
            )
        )

        assertThat(result).isNull()
    }

    @Test
    fun `invalid unread count does not reject otherwise valid payload`() {
        val result = parser.parse(
            mapOf(
                "event_id" to "\$event:example.org",
                "room_id" to "!room:example.org",
                "unread" to "not-a-number",
                "cs" to "client-secret",
            )
        )

        assertThat(result).isNotNull()
        assertThat(result?.unread).isNull()
    }
}
