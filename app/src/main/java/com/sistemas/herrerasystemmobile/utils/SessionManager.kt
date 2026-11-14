package com.sistemas.herrerasystemmobile.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Base64
import org.json.JSONObject

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
    private val _roleId = MutableStateFlow(
        preferences.getInt(KEY_ROLE_ID, roleIdFromToken(token).takeIf { it != null } ?: UNKNOWN_ROLE_ID)
            .takeIf { it != UNKNOWN_ROLE_ID }
    )
    val roleId: StateFlow<Int?> = _roleId.asStateFlow()

    val token: String?
        get() = preferences.getString(KEY_TOKEN, null)

    val isAuthenticated: Boolean
        get() = token?.let(::isValidJwt) == true

    private fun isValidJwt(value: String): Boolean {
        return try {
            val parts = value.split('.')
            if (parts.size != 3) false
            else {
                val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING))
                val expiration = JSONObject(payload).optLong("exp", 0L)
                expiration > System.currentTimeMillis() / 1000
            }
        } catch (_: Exception) { false }
    }

    @Synchronized
    fun saveSession(token: String, userName: String, roleId: Int? = null) {
        require(token.isNotBlank()) { "El token no puede estar vacío" }
        val resolvedRoleId = roleId ?: roleIdFromToken(token)
        preferences.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_NAME, userName)
            .apply { if (resolvedRoleId == null) remove(KEY_ROLE_ID) else putInt(KEY_ROLE_ID, resolvedRoleId) }
            .apply()
        _userName.value = userName
        _roleId.value = resolvedRoleId
    }

    @Synchronized
    fun clearSession() {
        preferences.edit().clear().apply()
        _userName.value = null
        _roleId.value = null
    }

    private fun roleIdFromToken(value: String?): Int? {
        return try {
            val payloadPart = value?.split('.')?.getOrNull(1)
            if (payloadPart == null) null else {
                val payload = JSONObject(String(Base64.decode(payloadPart, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)))
                val idKeys = listOf("roleId", "role_id", "RoleId", "idRol", "rolId")
                idKeys.firstNotNullOfOrNull { key -> payload.optString(key).toIntOrNull() }
                    ?: payload.keys().asSequence().mapNotNull { key ->
                        if (key.endsWith("/role", ignoreCase = true) || key.equals("role", true) || key.equals("roles", true)) {
                            val claim = payload.opt(key)
                            when (claim) {
                                is org.json.JSONArray -> (0 until claim.length()).mapNotNull { claim.optString(it).toRoleId() }.firstOrNull()
                                else -> claim?.toString()?.toRoleId()
                            }
                        } else null
                    }.firstOrNull()
            }
        } catch (_: Exception) { null }
    }

    private fun String.toRoleId(): Int? = toIntOrNull() ?: when {
        contains("admin", ignoreCase = true) -> ADMIN_ROLE_ID
        contains("vendedor", ignoreCase = true) || contains("seller", ignoreCase = true) -> VENDOR_ROLE_ID
        else -> null
    }

    private companion object {
        const val PREFERENCES_FILE = "herrera_secure_session"
        const val KEY_TOKEN = "jwt_token"
        const val KEY_USER_NAME = "user_name"
        const val KEY_ROLE_ID = "role_id"
        const val UNKNOWN_ROLE_ID = -1
        const val ADMIN_ROLE_ID = 1
        const val VENDOR_ROLE_ID = 2
    }
}
