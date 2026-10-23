package com.sistemas.herrerasystemmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.sistemas.herrerasystemmobile.data.remote.dto.*
import com.sistemas.herrerasystemmobile.ui.auth.LoginScreen
import com.sistemas.herrerasystemmobile.ui.common.AppDrawerContent
import com.sistemas.herrerasystemmobile.ui.common.UiState
import com.sistemas.herrerasystemmobile.ui.dashboard.AdminDashboardScreen
import com.sistemas.herrerasystemmobile.ui.theme.HerreraSystemMobileTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HerreraSystemMobileTheme {
                var isLogged by remember { mutableStateOf(false) }
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                var currentRoute by remember { mutableStateOf("dashboard") }

                if (!isLogged) {
                    LoginScreen(
                        onLoginClick = { usuario, password ->
                            isLogged = true
                        }
                    )
                } else {
                    // Contenedor global del menú desplegable
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            AppDrawerContent(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    currentRoute = route
                                    scope.launch { drawerState.close() }
                                },
                                onLogoutClick = {
                                    scope.launch { drawerState.close() }
                                    isLogged = false
                                }
                            )
                        }
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val sampleDashboardData = AdminDashboardDataDto(
                                adminName = "Nohelia",
                                userRole = "Marcelo - Administrador",
                                dateText = "domingo, 16 de agosto de 2026",
                                quickAccess = emptyList(),
                                kpis = emptyList(),
                                recentTransactions = emptyList()
                            )

                            // Vista del Dashboard con soporte para abrir el menú
                            AdminDashboardScreen(
                                state = UiState.Success(sampleDashboardData),
                                onOpenDrawer = {
                                    scope.launch { drawerState.open() }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}