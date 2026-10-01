/*
 * Copyright 2026 ZhiFlo.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.pushproviders.chinapush

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

interface ChinaPushStore {
    fun getToken(provider: String): String?
    fun storeToken(provider: String, token: String?)
    fun getSelectedProvider(): String?
    fun setSelectedProvider(provider: String?)
    fun getKnownProviders(): List<String>
}

@ContributesBinding(AppScope::class)
class SharedPreferencesChinaPushStore(
    private val sharedPreferences: SharedPreferences,
) : ChinaPushStore {
    override fun getToken(provider: String): String? {
        return sharedPreferences.getString(tokenKey(provider), null)
    }

    override fun storeToken(provider: String, token: String?) {
        sharedPreferences.edit {
            putString(tokenKey(provider), token)
        }
    }

    override fun getSelectedProvider(): String? {
        return sharedPreferences.getString(PREFS_KEY_SELECTED_PROVIDER, null)
    }

    override fun setSelectedProvider(provider: String?) {
        sharedPreferences.edit {
            putString(PREFS_KEY_SELECTED_PROVIDER, provider)
        }
    }

    override fun getKnownProviders(): List<String> {
        return ChinaPushConfig.providers
            .map { it.id }
            .filter { getToken(it).isNullOrBlank().not() }
    }

    private fun tokenKey(provider: String) = "CHINA_PUSH_TOKEN_${provider.uppercase()}"

    companion object {
        private const val PREFS_KEY_SELECTED_PROVIDER = "CHINA_PUSH_SELECTED_PROVIDER"
    }
}
