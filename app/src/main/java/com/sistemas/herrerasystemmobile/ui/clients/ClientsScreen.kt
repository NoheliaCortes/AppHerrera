package com.sistemas.herrerasystemmobile.ui.clients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sistemas.herrerasystemmobile.data.remote.dto.ClientDto
import com.sistemas.herrerasystemmobile.data.remote.dto.ClientCreateDto
import com.sistemas.herrerasystemmobile.data.remote.dto.ClientUpdateDto
import com.sistemas.herrerasystemmobile.ui.common.UiState
import com.sistemas.herrerasystemmobile.ui.dashboard.BottomNavigationBar
import com.sistemas.herrerasystemmobile.ui.theme.*
import com.sistemas.herrerasystemmobile.ui.viewmodel.ClientsUiData
import kotlin.math.absoluteValue

private val AvatarColors = listOf(Color(0xFFF2787C), Color(0xFF9B72DD), Color(0xFFE6B42E), Color(0xFF5688F3), Color(0xFF16B899))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(state: UiState<ClientsUiData>, query: String, filter: String,
    onSearch: (String) -> Unit, onFilter: (String) -> Unit, onRefresh: () -> Unit,
    onOpenDrawer: () -> Unit, onOpenDashboard: () -> Unit, onOpenProducts: () -> Unit, onOpenClients: () -> Unit = {},
    onCreate: (ClientCreateDto) -> Unit = {}, onUpdate: (Int, ClientUpdateDto) -> Unit = { _, _ -> }, onDelete: (Int) -> Unit = {}) {
    var editing by remember { mutableStateOf<ClientDto?>(null) }
    var showForm by remember { mutableStateOf(false) }
    Scaffold(bottomBar = { BottomNavigationBar(onOpenDrawer, onOpenProducts, false, onOpenDashboard, onOpenClients, true) }) { padding ->
        when (state) {
            UiState.Loading -> Box(Modifier.fillMaxSize().padding(padding).background(ScreenBackground), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryGreenButton) }
            is UiState.Error -> Column(Modifier.fillMaxSize().padding(padding).background(ScreenBackground), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text(state.message); Button(onClick = onRefresh) { Text("Reintentar") } }
            is UiState.Success -> {
                val data = state.data
                val all = data.clients
                val shown = all.filter { filter == "Todos" || (filter == "Activos" && it.isActive == true) || (filter == "Inactivos" && it.isActive != true) }
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).background(ScreenBackground),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    item {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(HeaderGradientEnd), contentAlignment = Alignment.Center) { Text("H", color = Color.White, fontWeight = FontWeight.Bold) }
                                Spacer(Modifier.width(9.dp))
                                Column {
                                    Text("Sorbetería Herrera", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    Text("Gestión de clientes", fontSize = 10.sp, color = TextMuted)
                                }
                            }
                            IconButton(onClick = { editing = null; showForm = true }, modifier = Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(PrimaryGreenButton)) {
                                Icon(Icons.Default.Add, "Agregar cliente", tint = Color.White)
                            }
                        }
                    }
                    item {
                        Column {
                            Text("Clientes", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text("Consulta y administra tu cartera de clientes.", fontSize = 11.sp, color = TextMuted)
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ClientStatCard("Activos", data.stats?.activeCustomers ?: all.count { it.isActive == true }, KpiGreenBg, PrimaryGreenButton, Icons.Default.PeopleAlt, Modifier.weight(1f))
                            ClientStatCard("Municipios", data.stats?.distinctMunicipalitiesWithCustomers ?: all.map { it.municipalityId }.distinct().size, KpiRedBg, Color(0xFFEF6670), Icons.Default.LocationOn, Modifier.weight(1f))
                            ClientStatCard("Puntos de venta", data.stats?.activePointsOfSale ?: all.mapNotNull { it.pointOfSale }.distinct().size, TagReabastecimientoBg, Color(0xFF8B5CF6), Icons.Default.Storefront, Modifier.weight(1f))
                        }
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = query, onValueChange = onSearch, modifier = Modifier.weight(1f).height(50.dp),
                                singleLine = true, shape = RoundedCornerShape(13.dp),
                                placeholder = { Text("Buscar clientes...", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = InputBackground, focusedContainerColor = CardBackground, unfocusedBorderColor = Color.Transparent, focusedBorderColor = PrimaryGreenButton),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                            )
                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = { expanded = true }, modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(CardBackground)) { Icon(Icons.Default.FilterList, "Filtrar clientes", tint = TextDark) }
                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    listOf("Todos", "Activos", "Inactivos").forEach { option ->
                                        DropdownMenuItem(text = { Text(option) }, onClick = { onFilter(option); expanded = false })
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text("Lista de Clientes", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                Text("${shown.size}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryGreenButton, modifier = Modifier.clip(CircleShape).background(KpiGreenBg).padding(horizontal = 7.dp, vertical = 2.dp))
                            }
                            Text(filter, fontSize = 10.sp, color = TextMuted)
                        }
                    }
                    if (shown.isEmpty()) item {
                        Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(if (query.isBlank()) "Aún no hay clientes" else "No encontramos clientes", fontWeight = FontWeight.SemiBold, color = TextDark)
                                Text("Prueba otra búsqueda o agrega un cliente.", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }
                    items(shown, key = { it.id }) { client ->
                        ClientCard(client, onEdit = { editing = client; showForm = true }, onDelete = { onDelete(client.id) })
                    }
                }
            }
        }
    }
    if (showForm) ClientFormDialog(editing, onDismiss = { showForm = false }, onSave = { municipality, first, last, phone, pos, address, active ->
        val current = editing
        if (current == null) onCreate(ClientCreateDto(municipality, first, last, phone.ifBlank { null }, pos.ifBlank { null }, address.ifBlank { null }))
        else onUpdate(current.id, ClientUpdateDto(municipality, first, last, phone.ifBlank { null }, pos.ifBlank { null }, address.ifBlank { null }, active))
        showForm = false
    })
}

@Composable
private fun ClientStatCard(label: String, value: Int, background: Color, accent: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(58.dp), shape = RoundedCornerShape(13.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(Modifier.fillMaxSize().padding(horizontal = 7.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Box(Modifier.size(27.dp).clip(RoundedCornerShape(9.dp)).background(background), contentAlignment = Alignment.Center) { Icon(icon, null, tint = accent, modifier = Modifier.size(15.dp)) }
            Spacer(Modifier.width(6.dp))
            Column {
                Text(label, fontSize = 8.sp, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(value.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }
        }
    }
}

@Composable
private fun ClientCard(client: ClientDto, onEdit: () -> Unit, onDelete: () -> Unit) {
    var menuOpen by remember { mutableStateOf(false) }
    val initials = "${client.firstName.firstOrNull()?.uppercaseChar() ?: 'C'}${client.lastName.firstOrNull()?.uppercaseChar() ?: ' '}"
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(start = 9.dp, end = 4.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(AvatarColors[(client.id.absoluteValue) % AvatarColors.size]), contentAlignment = Alignment.Center) {
                Text(initials, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                Text("${client.firstName} ${client.lastName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = TextMuted, modifier = Modifier.size(11.dp))
                    Text("${client.municipalityName} (${client.departmentName})", fontSize = 9.sp, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Business, null, tint = TextMuted, modifier = Modifier.size(10.dp))
                    Text(client.pointOfSale ?: client.phone ?: "Sin punto de venta", fontSize = 9.sp, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (client.isActive == false) Text(" · Inactivo", fontSize = 9.sp, color = Color(0xFFEF6670))
                }
            }
            Box {
                IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.MoreVert, "Acciones", tint = TextMuted, modifier = Modifier.size(17.dp)) }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(text = { Text("Editar") }, onClick = { menuOpen = false; onEdit() })
                    DropdownMenuItem(text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) }, onClick = { menuOpen = false; onDelete() })
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFB4BECD), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun ClientFormDialog(client: ClientDto?, onDismiss: () -> Unit,
    onSave: (Int, String, String, String, String, String, Boolean) -> Unit) {
    var municipality by remember(client) { mutableStateOf(client?.municipalityId?.toString() ?: "") }
    var first by remember(client) { mutableStateOf(client?.firstName ?: "") }
    var last by remember(client) { mutableStateOf(client?.lastName ?: "") }
    var phone by remember(client) { mutableStateOf(client?.phone.orEmpty()) }
    var point by remember(client) { mutableStateOf(client?.pointOfSale.orEmpty()) }
    var address by remember(client) { mutableStateOf(client?.posaddress.orEmpty()) }
    var active by remember(client) { mutableStateOf(client?.isActive != false) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(if (client == null) "Nuevo cliente" else "Editar cliente") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(municipality, { municipality = it }, label = { Text("ID municipio") }, singleLine = true)
            OutlinedTextField(first, { first = it }, label = { Text("Nombre") }, singleLine = true)
            OutlinedTextField(last, { last = it }, label = { Text("Apellido") }, singleLine = true)
            OutlinedTextField(phone, { phone = it }, label = { Text("Teléfono") }, singleLine = true)
            OutlinedTextField(point, { point = it }, label = { Text("Punto de venta") }, singleLine = true)
            OutlinedTextField(address, { address = it }, label = { Text("Dirección") }, singleLine = true)
            if (client != null) Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(active, { active = it }); Text("Activo") }
        } }, confirmButton = { TextButton(enabled = municipality.toIntOrNull() != null && first.isNotBlank() && last.isNotBlank(), onClick = { onSave(municipality.toInt(), first.trim(), last.trim(), phone.trim(), point.trim(), address.trim(), active) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}
