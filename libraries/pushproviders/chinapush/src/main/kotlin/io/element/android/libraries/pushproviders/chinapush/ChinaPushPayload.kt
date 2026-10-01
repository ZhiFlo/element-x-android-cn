/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush

import org.json.JSONObject

internal fun String?.toChinaPushPayloadMap(): Map<String, String?> {
    if (isNullOrBlank()) return emptyMap()
    return runCatching {
        val json = JSONObject(this)
        buildMap {
            val keys = json.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val value = if (json.isNull(key)) null else json.opt(key)?.toString()
                put(key, value)
            }
        }
    }.getOrDefault(emptyMap())
}
