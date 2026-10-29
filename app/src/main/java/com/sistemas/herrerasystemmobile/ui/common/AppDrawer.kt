package com.sistemas.herrerasystemmobile.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sistemas.herrerasystemmobile.ui.theme.*

@Composable
fun AppDrawerContent(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogoutClick: () -> Unit,
    userName: String = "Marcelo Mena",
    userRole: String = "Administrador",
    userInitials: String = "NC"
) {
    ModalDrawerSheet(
        drawerContainerColor = ScreenBackground,
        drawerShape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
        modifier = Modifier.width(300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Cabecera Verde Menta
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(HeaderGradientStart, HeaderGradientEnd)
                        )
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("H", color = HeaderGradientEnd, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Sorbetería Herrera", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Hecho en familia", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Tarjeta del Usuario
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreenButton),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(userInitials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(userName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                            Text(userRole, fontSize = 11.sp, color = TextMuted)
                        }
                    }
                    Icon(
                        Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. SECCIÓN GESTIÓN
            DrawerSectionTitle("GESTIÓN")
            DrawerMenuItem("Productos", Icons.Outlined.Inventory2, "products", currentRoute, onNavigate)
            DrawerMenuItem("Reportes", Icons.Outlined.BarChart, "reports", currentRoute, onNavigate)
            DrawerMenuItem("Precios", Icons.Outlined.Sell, "precios", currentRoute, onNavigate)
            DrawerMenuItem("Info del Producto", Icons.Outlined.Layers, "info_producto", currentRoute, onNavigate)
            DrawerMenuItem("Inventario", Icons.Outlined.Inventory2, "inventario", currentRoute, onNavigate)
            DrawerMenuItem("Transacciones", Icons.Outlined.Paid, "transacciones", currentRoute, onNavigate)
            DrawerMenuItem("Movimientos de Inventario", Icons.Outlined.LocalShipping, "movimientos", currentRoute, onNavigate)

            Spacer(modifier = Modifier.height(16.dp))

            // 4. SECCIÓN SISTEMA
            DrawerSectionTitle("SISTEMA")
            DrawerMenuItem("Usuarios", Icons.Outlined.PeopleOutline, "usuarios", currentRoute, onNavigate)
            DrawerMenuItem("Tema", Icons.Outlined.Palette, "tema", currentRoute, onNavigate)
            DrawerMenuItem("Logs del Sistema", Icons.Outlined.Lock, "logs", currentRoute, onNavigate)
            DrawerMenuItem("Configuración", Icons.Outlined.Settings, "configuracion", currentRoute, onNavigate)

            Spacer(modifier = Modifier.height(16.dp))

            // 5. SECCIÓN SESIÓN
            DrawerSectionTitle("SESIÓN")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogoutClick() }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Cerrar sesión",
                    color = Color(0xFFEF4444),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DrawerSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextMuted,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
    )
}

@Composable
private fun DrawerMenuItem(
    title: String,
    icon: ImageVector,
    route: String,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val isSelected = currentRoute == route
    val tintColor = if (isSelected) PrimaryGreenButton else TextDark

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate(route) }
            .padding(horizontal = 24.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = tintColor
        )
    }
}
