package com.sistemas.herrerasystemmobile.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sistemas.herrerasystemmobile.data.remote.dto.*
import com.sistemas.herrerasystemmobile.ui.common.UiState
import com.sistemas.herrerasystemmobile.ui.theme.*

@Composable
fun AdminDashboardScreen(
    state: UiState<AdminDashboardDataDto>,
    onRefresh: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    onOpenProducts: () -> Unit = {}
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(onOpenDrawer = onOpenDrawer, onOpenProducts = onOpenProducts) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ScreenBackground)
        ) {
            when (state) {
                is UiState.Loading -> DashboardLoadingView()
                is UiState.Error -> DashboardErrorView(message = state.message, onRetry = onRefresh)
                is UiState.Success -> DashboardContentView(data = state.data, onOpenDrawer = onOpenDrawer)
            }
        }
    }
}

@Composable
private fun DashboardContentView(
    data: AdminDashboardDataDto,
    onOpenDrawer: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Top Header con Avatar touchable para desplegar menú
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(HeaderGradientEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("H", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Sorbetería Herrera", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                        Text("Hecho en familia", fontSize = 11.sp, color = TextMuted)
                    }
                }

                // Avatar interactivo
                IconButton(onClick = onOpenDrawer) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreenButton),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("MA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Banner Saludo
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(HeaderGradientStart, HeaderGradientEnd)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("¡Hola, ${data.adminName}! 👋", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("${data.dateText} · ${data.userRole}", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("H", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Accesos Rápidos
        item {
            Text("Accesos rápidos", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickAccessButton("Info Prod.", KpiGreenBg, PrimaryGreenButton)
                QuickAccessButton("Precios", KpiRedBg, Color(0xFF8B5CF6))
                QuickAccessButton("Inventario", KpiBlueBg, TagPedidoText)
                QuickAccessButton("Transacc.", KpiGreenBg, PrimaryGreenButton)
            }
        }

        // Sección KPIs (Matriz 2x2)
        item {
            Text("Indicadores clave (SSAS)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    KpiCardView("Ventas del día", "C$4,860", KpiGreenBg, PrimaryGreenButton, modifier = Modifier.weight(1f))
                    KpiCardView("Pedidos pendientes", "1", KpiBlueBg, TagPedidoText, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    KpiCardView("Stock crítico", "8", KpiRedBg, Color(0xFFEF4444), modifier = Modifier.weight(1f))
                    KpiCardView("Ticket promedio", "C$353", KpiYellowBg, Color(0xFFD97706), modifier = Modifier.weight(1f))
                }
            }
        }

        // Espacio Reservado para los Gráficos Analíticos
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Rendimiento analítico", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📊 Espacio reservado para Gráficos SSAS\n(Top Productos / Ventas por Línea)",
                        color = TextMuted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun KpiCardView(
    title: String,
    value: String,
    bgColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AttachMoney, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
            }
            Column {
                Text(title, fontSize = 11.sp, color = TextMuted)
                Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }
        }
    }
}

@Composable
fun QuickAccessButton(title: String, bgColor: Color, iconColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Widgets, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(title, fontSize = 10.sp, color = TextMuted)
    }
}

@Composable
fun BottomNavigationBar(
    onOpenDrawer: () -> Unit = {},
    onOpenProducts: () -> Unit = {},
    productsSelected: Boolean = false
) {
    NavigationBar(containerColor = CardBackground) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.GridView, contentDescription = null) },
            label = { Text("Inicio", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = productsSelected,
            onClick = onOpenProducts,
            icon = { Icon(Icons.Default.People, contentDescription = null) },
            label = { Text("Clientes", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Inventory2, contentDescription = null) },
            label = { Text("Productos", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = false,
            onClick = onOpenDrawer, // Al pulsar "Más", abre el menú lateral
            icon = { Icon(Icons.Default.Menu, contentDescription = null) },
            label = { Text("Más", fontSize = 10.sp) }
        )
    }
}

@Composable
fun DashboardLoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = PrimaryGreenButton)
    }
}

@Composable
fun DashboardErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Error al cargar el panel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(message, fontSize = 12.sp, color = TextMuted, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreenButton)) {
            Text("Reintentar")
        }
    }
}
