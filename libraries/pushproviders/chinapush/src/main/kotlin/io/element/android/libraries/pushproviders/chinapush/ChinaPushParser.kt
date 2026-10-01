/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush

import dev.zacsweers.metro.Inject
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.pushproviders.api.PushData

@Inject
class ChinaPushParser {
    fun parse(message: Map<String, String?>): PushData? {
        val eventId = message["event_id"]?.let(::EventId) ?: return null
        val roomId = message["room_id"]?.let(::RoomId) ?: return null
        val clientSecret = message["cs"] ?: return null
        return PushData(
            eventId = eventId,
            roomId = roomId,
            unread = message["unread"]?.toIntOrNull(),
            clientSecret = clientSecret,
        )
    }
}
