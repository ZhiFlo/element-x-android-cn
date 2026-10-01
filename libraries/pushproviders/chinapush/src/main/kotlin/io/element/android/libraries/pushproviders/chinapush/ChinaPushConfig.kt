/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush

object ChinaPushConfig {
    const val INDEX = 2
    const val NAME = "China Push"
    val PUSHER_HTTP_URL: String = BuildConfig.PUSHER_HTTP_URL

    val providers = listOf(
        Provider("hms", "Huawei Push"),
        Provider("honor", "Honor Push"),
        Provider("xiaomi", "Xiaomi Push"),
        Provider("oppo", "OPPO Push"),
        Provider("vivo", "vivo Push"),
    )

    data class Provider(
        val id: String,
        val displayName: String,
    )
}
