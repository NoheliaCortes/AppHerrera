package com.sistemas.herrerasystemmobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sistemas.herrerasystemmobile.data.repository.AuthRepository
import com.sistemas.herrerasystemmobile.ui.common.UiState
import com.sistemas.herrerasystemmobile.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _loginState = MutableStateFlow<UiState<String>>(UiState.Success(""))
    val loginState: StateFlow<UiState<String>> = _loginState.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = UiState.Error("Ingresa tu usuario y contraseña")
            return
        }

        viewModelScope.launch {
            _loginState.value = UiState.Loading
            repository.login(username, password)
                .onSuccess { user ->
                    sessionManager.saveSession(user.token, user.displayName)
                    _loginState.value = UiState.Success(user.displayName)
                }
                .onFailure { error ->
                    _loginState.value = UiState.Error(
                        error.message?.takeIf(String::isNotBlank) ?: "Error al conectar con Azure"
                    )
                }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _loginState.value = UiState.Success("")
    }
}
