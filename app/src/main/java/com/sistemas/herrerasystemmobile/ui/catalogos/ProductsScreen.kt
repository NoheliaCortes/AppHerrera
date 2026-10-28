package com.sistemas.herrerasystemmobile.ui.catalogos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sistemas.herrerasystemmobile.data.remote.dto.ProductCatalogDto
import com.sistemas.herrerasystemmobile.data.remote.dto.ProductStatsDto
import com.sistemas.herrerasystemmobile.ui.common.UiState
import com.sistemas.herrerasystemmobile.ui.dashboard.BottomNavigationBar
import com.sistemas.herrerasystemmobile.ui.theme.*
import com.sistemas.herrerasystemmobile.ui.viewmodel.ProductsUiData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    state: UiState<ProductsUiData>,
    searchQuery: String,
    selectedCategory: String,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onRefresh: () -> Unit,
    onOpenDrawer: () -> Unit = {}
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(onOpenDrawer = onOpenDrawer) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ScreenBackground)
        ) {
            when (state) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryGreenButton)
                    }
                }
                is UiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Error de conexión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(state.message, fontSize = 12.sp, color = TextMuted, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onRefresh, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreenButton)) {
                            Text("Reintentar")
                        }
                    }
                }
                is UiState.Success -> {
                    ProductsContent(
                        data = state.data,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        onSearchChange = onSearchChange,
                        onCategorySelect = onCategorySelect
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductsContent(
    data: ProductsUiData,
    searchQuery: String,
    selectedCategory: String,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Cabecera Superior
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
                    Text("Catálogo de productos", fontSize = 11.sp, color = TextMuted)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Productos", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text("Administra el catálogo completo de productos.", fontSize = 12.sp, color = TextMuted)

        Spacer(modifier = Modifier.height(14.dp))

        // Tarjetas de Resumen KPI (Registrados / Activos)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Registrados",
                count = "${data.stats?.totalProducts ?: data.products.size}",
                bgColor = KpiGreenBg,
                iconColor = PrimaryGreenButton,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Activos",
                count = "${data.stats?.activeProducts ?: data.products.count { it.isActive == true }}",
                bgColor = Color(0xFFF3E8FF),
                iconColor = Color(0xFF8B5CF6),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chips de Filtros (Todas, Tradicionales, Lights, Nieves)
        val categories = listOf("Todas", "Tradicionales", "Lights", "Nieves")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = category == selectedCategory
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) PrimaryGreenButton else CardBackground,
                    modifier = Modifier.clickable { onCategorySelect(category) }
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) Color.White else TextDark,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Campo de Búsqueda + Botón Filtro
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Buscar por nombre...", fontSize = 13.sp, color = TextMuted) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = TextMuted) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFEEF2F6),
                    unfocusedContainerColor = Color(0xFFEEF2F6),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CardBackground,
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Tune, contentDescription = "Filtros", tint = TextDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Contador de Resultados
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Resultados", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = PrimaryGreenButton,
                shape = CircleShape
            ) {
                Text(
                    text = "${data.products.size}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grid 2 Columnas de Tarjetas de Productos
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(data.products) { product ->
                ProductCard(product = product)
            }
        }
    }
}

@Composable
fun StatCard(title: String, count: String, bgColor: Color, iconColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(26.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = iconColor, modifier = Modifier.size(14.dp))
            }
            Column {
                Text(title, fontSize = 10.sp, color = TextMuted)
                Text(count, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }
        }
    }
}

@Composable
fun ProductCard(product: ProductCatalogDto) {
    val azureOrigin = "https://tu-appservice-name.azurewebsites.net"
    val fullImageUrl = remember(product.imageUrl) {
        when {
            product.imageUrl.isNull_or_blank() -> null
            product.imageUrl.startsWith("http") -> product.imageUrl
            else -> "$azureOrigin${product.imageUrl}"
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().height(210.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.55f)
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                if (fullImageUrl != null) {
                    AsyncImage(
                        model = fullImageUrl,
                        contentDescription = product.productName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.Icecream, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                }

                // Tag de Activo
                if (product.isActive == true) {
                    Surface(
                        color = TagVentaBg,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Activo",
                            color = TagVentaText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(0.45f)
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = product.productName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                val priceDisplay = product.retailPrice ?: product.wholesalePrice ?: 0.0
                Text(
                    text = "C$${String.format("%.2f", priceDisplay)}",
                    color = PrimaryGreenButton,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Text(
                    text = product.lineName,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
    }
}

// Extensión utilitaria para cadenas nulas o vacías
private fun String?.isNull_or_blank(): Boolean = this == null || this.isBlank()