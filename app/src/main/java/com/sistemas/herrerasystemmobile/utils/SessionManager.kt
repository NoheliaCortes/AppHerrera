package com.sistemas.herrerasystemmobile.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Stores the small amount of session state needed by the app, encrypted at rest. */
class SessionManager(context: Context) {
    private val preferences = EncryptedSharedPreferences.create(
        context.applicationContext,
        PREFERENCES_FILE,
        MasterKey.Builder(context.applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _userName = MutableStateFlow(preferences.getString(KEY_USER_NAME, null))
    val userName: StateFlow<String?> = _userName.asStateFlow()

    val token: String?
        get() = preferences.getString(KEY_TOKEN, null)

    val isAuthenticated: Boolean
        get() = !token.isNullOrBlank()

    @Synchronized
    fun saveSession(token: String, userName: String) {
        require(token.isNotBlank()) { "El token no puede estar vacío" }
        preferences.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_NAME, userName)
            .apply()
        _userName.value = userName
    }

    @Synchronized
    fun clearSession() {
        preferences.edit().clear().apply()
        _userName.value = null
    }

    private companion object {
        const val PREFERENCES_FILE = "herrera_secure_session"
        const val KEY_TOKEN = "jwt_token"
        const val KEY_USER_NAME = "user_name"
    }
}
