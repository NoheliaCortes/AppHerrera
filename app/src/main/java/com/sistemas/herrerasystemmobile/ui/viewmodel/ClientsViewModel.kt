package com.sistemas.herrerasystemmobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sistemas.herrerasystemmobile.data.remote.dto.*
import com.sistemas.herrerasystemmobile.data.repository.ClientRepository
import com.sistemas.herrerasystemmobile.ui.common.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ClientsUiData(val clients: List<ClientDto> = emptyList(), val stats: ClientStatsDto? = null)

class ClientsViewModel(private val repository: ClientRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<ClientsUiData>>(UiState.Loading)
    val uiState: StateFlow<UiState<ClientsUiData>> = _uiState
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query
    private val _activeFilter = MutableStateFlow("Todos")
    val activeFilter: StateFlow<String> = _activeFilter
    private var searchJob: Job? = null

    init { load() }
    fun load() = viewModelScope.launch {
        _uiState.value = UiState.Loading
        val clients = repository.getClients(_query.value.ifBlank { null })
        if (clients.isFailure) _uiState.value = UiState.Error(clients.exceptionOrNull()?.message ?: "No se pudieron cargar clientes")
        else {
            val stats = repository.getStats()
            _uiState.value = UiState.Success(ClientsUiData(clients.getOrDefault(emptyList()), stats.getOrNull()))
        }
    }
    fun search(value: String) { _query.value = value; searchJob?.cancel(); searchJob = viewModelScope.launch { delay(350); load() } }
    fun filter(value: String) { _activeFilter.value = value }
    fun create(dto: ClientCreateDto, done: (Result<ClientDto>) -> Unit) = viewModelScope.launch { done(repository.create(dto)); load() }
    fun update(id: Int, dto: ClientUpdateDto, done: (Result<Unit>) -> Unit) = viewModelScope.launch { done(repository.update(id, dto)); load() }
    fun delete(id: Int, done: (Result<Unit>) -> Unit) = viewModelScope.launch { done(repository.delete(id)); load() }
}
