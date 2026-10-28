package com.sistemas.herrerasystemmobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sistemas.herrerasystemmobile.data.remote.dto.ProductCatalogDto
import com.sistemas.herrerasystemmobile.data.remote.dto.ProductStatsDto
import com.sistemas.herrerasystemmobile.data.repository.CatalogRepository
import com.sistemas.herrerasystemmobile.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProductsUiData(
    val stats: ProductStatsDto? = null,
    val products: List<ProductCatalogDto> = emptyList()
)

class ProductsViewModel(private val repository: CatalogRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ProductsUiData>>(UiState.Loading)
    val uiState: StateFlow<UiState<ProductsUiData>> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow("Todas")
    val selectedCategory: StateFlow<String> = _selectedCategory

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val statsResult = repository.getProductStats()
            val catalogResult = repository.getProductCatalog(
                search = _searchQuery.value.ifBlank { null }
            )

            if (catalogResult.isSuccess) {
                _uiState.value = UiState.Success(
                    ProductsUiData(
                        stats = statsResult.getOrNull(),
                        products = catalogResult.getOrDefault(emptyList())
                    )
                )
            } else {
                _uiState.value = UiState.Error(
                    catalogResult.exceptionOrNull()?.message ?: "Error al conectar con Azure"
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        loadData()
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
        // Aquí se puede agregar mapeo de categoría a lineId según la BD
        loadData()
    }
}