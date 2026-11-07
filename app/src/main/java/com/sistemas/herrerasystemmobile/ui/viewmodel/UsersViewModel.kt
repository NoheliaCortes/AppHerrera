package com.sistemas.herrerasystemmobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sistemas.herrerasystemmobile.data.remote.dto.*
import com.sistemas.herrerasystemmobile.data.repository.UserRepository
import com.sistemas.herrerasystemmobile.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UsersUiData(val users: List<UserDto> = emptyList())

class UsersViewModel(private val repository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<UsersUiData>>(UiState.Loading)
    val uiState: StateFlow<UiState<UsersUiData>> = _uiState
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    init { load() }
    fun search(value: String) { _query.value = value }
    fun load() = viewModelScope.launch {
        _uiState.value = UiState.Loading
        repository.getUsers().onSuccess { _uiState.value = UiState.Success(UsersUiData(it)) }
            .onFailure { _uiState.value = UiState.Error(it.message ?: "No se pudieron cargar los usuarios") }
    }
    fun create(dto: CreateUserDto, done: (Result<Unit>) -> Unit) = viewModelScope.launch { val result = repository.create(dto); done(result); if (result.isSuccess) load() }
    fun update(id: Int, dto: UpdateUserDto, done: (Result<Unit>) -> Unit) = viewModelScope.launch { val result = repository.update(id, dto); done(result); if (result.isSuccess) load() }
    fun toggleStatus(id: Int, done: (Result<Unit>) -> Unit) = viewModelScope.launch { val result = repository.toggleStatus(id); done(result); if (result.isSuccess) load() }
    fun resetPassword(id: Int, password: String, done: (Result<Unit>) -> Unit) = viewModelScope.launch { done(repository.resetPassword(id, password)) }
}
