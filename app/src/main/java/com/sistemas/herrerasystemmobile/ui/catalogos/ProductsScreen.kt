package com.sistemas.herrerasystemmobile.ui.catalogos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
    onOpenDrawer: () -> Unit = {},
    onOpenProducts: () -> Unit = {},
    onOpenDashboard: () -> Unit = {},
    onOpenClients: () -> Unit = {}
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(onOpenDrawer = onOpenDrawer, onOpenProducts = onOpenProducts, productsSelected = true, onOpenDashboard = onOpenDashboard, onOpenClients = onOpenClients) }
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
    val visibleProducts = remember(data.products, selectedCategory) {
        if (selectedCategory == "Todas") data.products
        else data.products.filter { it.lineName.contains(selectedCategory, ignoreCase = true) }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(HeaderGradientEnd), contentAlignment = Alignment.Center) {
                    Text("H", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Sorbetería Herrera", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Text("Catálogo de productos", fontSize = 11.sp, color = TextMuted)
                }
            }
        }
        item {
            Column {
                Text("Productos", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text("Administra el catálogo completo de productos.", fontSize = 12.sp, color = TextMuted)
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Registrados", "${data.stats?.totalProducts ?: data.products.size}", KpiGreenBg, PrimaryGreenButton, Modifier.weight(1f))
                StatCard("Activos", "${data.stats?.activeProducts ?: data.products.count { it.isActive == true }}", Color(0xFFF3E8FF), Color(0xFF8B5CF6), Modifier.weight(1f))
            }
        }
        item {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Todas", "Tradicionales", "Lights", "Nieves").forEach { category ->
                    val selected = category == selectedCategory
                    Surface(shape = RoundedCornerShape(20.dp), color = if (selected) PrimaryGreenButton else CardBackground,
                        modifier = Modifier.clickable { onCategorySelect(category) }) {
                        Text(category, color = if (selected) Color.White else TextDark, fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = searchQuery, onValueChange = onSearchChange,
                    placeholder = { Text("Buscar por nombre...", fontSize = 13.sp, color = TextMuted) },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color(0xFFEEF2F6), unfocusedContainerColor = Color(0xFFEEF2F6), focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent),
                    shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)
                )
                Surface(shape = RoundedCornerShape(12.dp), color = CardBackground, modifier = Modifier.size(50.dp)) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Tune, contentDescription = "Filtros", tint = TextDark) }
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Resultados", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                Spacer(Modifier.width(8.dp))
                Surface(color = PrimaryGreenButton, shape = CircleShape) {
                    Text("${visibleProducts.size}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                }
            }
        }
        items(visibleProducts.chunked(2)) { rowProducts ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowProducts.forEach { product -> ProductCard(product, Modifier.weight(1f)) }
                if (rowProducts.size == 1) Spacer(Modifier.weight(1f))
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
fun ProductCard(product: ProductCatalogDto, modifier: Modifier = Modifier) {
    val azureOrigin = com.sistemas.herrerasystemmobile.data.remote.retrofit.RetrofitClient.API_ORIGIN
    val fullImageUrl = remember(product.imageUrl) {
        val imageUrl = product.imageUrl
        when {
            imageUrl.isNullOrBlank() -> null
            imageUrl.startsWith("http://", ignoreCase = true) || imageUrl.startsWith("https://", ignoreCase = true) -> imageUrl
            else -> "$azureOrigin/${imageUrl.trimStart('/')}"
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth().height(210.dp)
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
                text = "${product.lineName} · ${product.flavorName} · ${product.presentationName}",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
    }
}

// Extensión utilitaria para cadenas nulas o vacías
private fun String?.isNull_or_blank(): Boolean = this == null || this.isBlank()
