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
import com.sistemas.herrerasystemmobile.data.repository.AuthRepository
import com.sistemas.herrerasystemmobile.data.repository.ClientRepository
import com.sistemas.herrerasystemmobile.data.repository.UserRepository
import com.sistemas.herrerasystemmobile.data.repository.ReportRepository
import com.sistemas.herrerasystemmobile.ui.clients.ClientsScreen
import com.sistemas.herrerasystemmobile.ui.users.UsersScreen
import com.sistemas.herrerasystemmobile.ui.reports.ReportsScreen
import com.sistemas.herrerasystemmobile.ui.auth.LoginScreen
import com.sistemas.herrerasystemmobile.ui.catalogos.ProductsScreen
import com.sistemas.herrerasystemmobile.ui.common.AppDrawerContent
import com.sistemas.herrerasystemmobile.ui.common.UiState
import com.sistemas.herrerasystemmobile.ui.dashboard.AdminDashboardScreen
import com.sistemas.herrerasystemmobile.ui.theme.HerreraSystemMobileTheme
import com.sistemas.herrerasystemmobile.ui.viewmodel.ProductsViewModel
import com.sistemas.herrerasystemmobile.ui.viewmodel.AuthViewModel
import com.sistemas.herrerasystemmobile.ui.viewmodel.ClientsViewModel
import com.sistemas.herrerasystemmobile.ui.viewmodel.UsersViewModel
import com.sistemas.herrerasystemmobile.ui.viewmodel.ReportsViewModel
import com.sistemas.herrerasystemmobile.utils.SessionManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HerreraSystemMobileTheme {
                val sessionManager = remember { SessionManager(applicationContext) }
                val apiService = remember(sessionManager) { RetrofitClient.getApiService(sessionManager) }
                var isLogged by remember { mutableStateOf(false) }
                var currentRoute by remember { mutableStateOf("dashboard") }
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val authViewModel: AuthViewModel = viewModel(
                    factory = remember {
                        object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                                AuthViewModel(AuthRepository(apiService), sessionManager) as T
                        }
                    }
                )
                val loginState by authViewModel.loginState.collectAsState()
                val userName by sessionManager.userName.collectAsState()
                val productsViewModel: ProductsViewModel = viewModel(
                    factory = remember {
                        object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ProductsViewModel(CatalogRepository(apiService)) as T
                            }
                        }
                    }
                )
                val productsState by productsViewModel.uiState.collectAsState()
                val searchQuery by productsViewModel.searchQuery.collectAsState()
                val selectedCategory by productsViewModel.selectedCategory.collectAsState()
                val clientsViewModel: ClientsViewModel = viewModel(
                    factory = remember { object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T = ClientsViewModel(ClientRepository(apiService)) as T
                    } }
                )
                val clientsState by clientsViewModel.uiState.collectAsState()
                val clientsQuery by clientsViewModel.query.collectAsState()
                val clientsFilter by clientsViewModel.activeFilter.collectAsState()
                val usersViewModel: UsersViewModel = viewModel(
                    factory = remember { object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T = UsersViewModel(UserRepository(apiService)) as T
                    } }
                )
                val usersState by usersViewModel.uiState.collectAsState()
                val usersQuery by usersViewModel.query.collectAsState()
                val reportsViewModel: ReportsViewModel = viewModel(
                    factory = remember { object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T = ReportsViewModel(ReportRepository(apiService)) as T
                    } }
                )
                val reportsState by reportsViewModel.uiState.collectAsState()
                val selectedReport by reportsViewModel.reportType.collectAsState()
                val reportYear by reportsViewModel.year.collectAsState()
                val reportMonth by reportsViewModel.month.collectAsState()

                if (!isLogged) {
                    LoginScreen(
                        loginState = loginState,
                        onLoginClick = authViewModel::login
                    )
                    LaunchedEffect(loginState) {
                        if (loginState is UiState.Success && sessionManager.isAuthenticated) {
                            isLogged = true
                            currentRoute = "dashboard"
                        }
                    }
                } else {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            AppDrawerContent(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    currentRoute = when (route) { "products" -> "products"; "clients" -> "clients"; "usuarios" -> "usuarios"; "reports" -> "reports"; else -> "dashboard" }
                                    if (currentRoute == "products") productsViewModel.loadData()
                                    if (currentRoute == "clients") clientsViewModel.load()
                                    if (currentRoute == "usuarios") usersViewModel.load()
                                    if (currentRoute == "reports") reportsViewModel.refresh()
                                    scope.launch { drawerState.close() }
                                },
                                onLogoutClick = {
                                    scope.launch { drawerState.close() }
                                    currentRoute = "dashboard"
                                    authViewModel.logout()
                                    isLogged = false
                                },
                                userName = userName ?: "Usuario"
                            )
                        }
                    ) {
                        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                            if (currentRoute == "reports") {
                                ReportsScreen(
                                    state = reportsState,
                                    selectedType = selectedReport,
                                    year = reportYear,
                                    month = reportMonth,
                                    onSelectType = reportsViewModel::selectReport,
                                    onSelectYear = reportsViewModel::selectYear,
                                    onSelectMonth = reportsViewModel::selectMonth,
                                    onRefresh = reportsViewModel::refresh,
                                    onBack = { currentRoute = "dashboard" }
                                )
                            } else if (currentRoute == "usuarios") {
                                UsersScreen(
                                    state = usersState,
                                    query = usersQuery,
                                    onSearch = usersViewModel::search,
                                    onRefresh = usersViewModel::load,
                                    onBack = { currentRoute = "dashboard" },
                                    onCreate = { dto, done -> usersViewModel.create(dto, done) },
                                    onUpdate = { id, dto, done -> usersViewModel.update(id, dto, done) },
                                    onToggleStatus = { id, done -> usersViewModel.toggleStatus(id, done) },
                                    onResetPassword = { id, password, done -> usersViewModel.resetPassword(id, password, done) }
                                )
                            } else if (currentRoute == "clients") {
                                ClientsScreen(clientsState, clientsQuery, clientsFilter,
                                    onSearch = clientsViewModel::search, onFilter = clientsViewModel::filter,
                                    onRefresh = { clientsViewModel.load() }, onOpenDrawer = { scope.launch { drawerState.open() } },
                                    onOpenDashboard = { currentRoute = "dashboard" },
                                    onOpenProducts = { currentRoute = "products"; productsViewModel.loadData() },
                                    onOpenClients = { currentRoute = "clients" },
                                    onCreate = { dto -> clientsViewModel.create(dto) { } },
                                    onUpdate = { id, dto -> clientsViewModel.update(id, dto) { } },
                                    onDelete = { id -> clientsViewModel.delete(id) { } })
                            } else if (currentRoute == "products") {
                                ProductsScreen(
                                    state = productsState,
                                    searchQuery = searchQuery,
                                    selectedCategory = selectedCategory,
                                    onSearchChange = productsViewModel::onSearchQueryChanged,
                                    onCategorySelect = productsViewModel::onCategorySelected,
                                    onRefresh = productsViewModel::loadData,
                                    onOpenDrawer = { scope.launch { drawerState.open() } },
                                    onOpenProducts = { currentRoute = "products" },
                                    onOpenDashboard = { currentRoute = "dashboard" },
                                    onOpenClients = { currentRoute = "clients" }
                                )
                            } else {
                                val dashboard = AdminDashboardDataDto(
                                    adminName = userName ?: "Usuario",
                                    userRole = "Administrador",
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
                                    }, onOpenDashboard = { currentRoute = "dashboard" }, onOpenClients = { currentRoute = "clients" }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
