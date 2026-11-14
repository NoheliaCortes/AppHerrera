package com.sistemas.herrerasystemmobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sistemas.herrerasystemmobile.data.repository.DashboardRepository
import com.sistemas.herrerasystemmobile.ui.common.UiState
import com.sistemas.herrerasystemmobile.data.remote.dto.AdminDashboardDataDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<AdminDashboardDataDto>>(UiState.Loading)
    val uiState: StateFlow<UiState<AdminDashboardDataDto>> = _uiState.asStateFlow()

    fun refresh(adminName: String = "Usuario") {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = repository.load(adminName).fold(
                { UiState.Success(it) },
                { UiState.Error(it.message ?: "No se pudo cargar el dashboard") }
            )
        }
    }
}
