package com.sistemas.herrerasystemmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sistemas.herrerasystemmobile.data.remote.dto.AdminDashboardDataDto
import com.sistemas.herrerasystemmobile.data.remote.retrofit.RetrofitClient
import com.sistemas.herrerasystemmobile.data.repository.CatalogRepository
import com.sistemas.herrerasystemmobile.ui.auth.LoginScreen
import com.sistemas.herrerasystemmobile.ui.catalogos.ProductsScreen
import com.sistemas.herrerasystemmobile.ui.common.AppDrawerContent
import com.sistemas.herrerasystemmobile.ui.common.UiState
import com.sistemas.herrerasystemmobile.ui.dashboard.AdminDashboardScreen
import com.sistemas.herrerasystemmobile.ui.theme.HerreraSystemMobileTheme
import com.sistemas.herrerasystemmobile.ui.viewmodel.ProductsViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HerreraSystemMobileTheme {
                var isLogged by remember { mutableStateOf(false) }
                var currentRoute by remember { mutableStateOf("dashboard") }
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val productsViewModel: ProductsViewModel = viewModel(
                    factory = remember {
                        object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ProductsViewModel(
                                    CatalogRepository(RetrofitClient.getApiService())
                                ) as T
                            }
                        }
                    }
                )
                val productsState by productsViewModel.uiState.collectAsState()
                val searchQuery by productsViewModel.searchQuery.collectAsState()
                val selectedCategory by productsViewModel.selectedCategory.collectAsState()

                if (!isLogged) {
                    LoginScreen(onLoginClick = { _, _ -> isLogged = true })
                } else {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            AppDrawerContent(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    currentRoute = if (route == "products") "products" else "dashboard"
                                    if (currentRoute == "products") productsViewModel.loadData()
                                    scope.launch { drawerState.close() }
                                },
                                onLogoutClick = {
                                    scope.launch { drawerState.close() }
                                    currentRoute = "dashboard"
                                    isLogged = false
                                }
                            )
                        }
                    ) {
                        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                            if (currentRoute == "products") {
                                ProductsScreen(
                                    state = productsState,
                                    searchQuery = searchQuery,
                                    selectedCategory = selectedCategory,
                                    onSearchChange = productsViewModel::onSearchQueryChanged,
                                    onCategorySelect = productsViewModel::onCategorySelected,
                                    onRefresh = productsViewModel::loadData,
                                    onOpenDrawer = { scope.launch { drawerState.open() } },
                                    onOpenProducts = { currentRoute = "products" }
                                )
                            } else {
                                val dashboard = AdminDashboardDataDto(
                                    adminName = "Nohelia",
                                    userRole = "Marcelo - Administrador",
                                    dateText = "domingo, 16 de agosto de 2026",
                                    quickAccess = emptyList(),
                                    kpis = emptyList(),
                                    recentTransactions = emptyList()
                                )
                                AdminDashboardScreen(
                                    state = UiState.Success(dashboard),
                                    onOpenDrawer = { scope.launch { drawerState.open() } },
                                    onOpenProducts = {
                                        currentRoute = "products"
                                        productsViewModel.loadData()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
