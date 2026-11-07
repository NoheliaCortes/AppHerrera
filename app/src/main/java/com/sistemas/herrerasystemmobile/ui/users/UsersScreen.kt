package com.sistemas.herrerasystemmobile.ui.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sistemas.herrerasystemmobile.data.remote.dto.*
import com.sistemas.herrerasystemmobile.ui.common.UiState
import com.sistemas.herrerasystemmobile.ui.theme.*
import com.sistemas.herrerasystemmobile.ui.viewmodel.UsersUiData
import kotlin.math.absoluteValue

private val UserAvatarColors = listOf(Color(0xFF10B993), Color(0xFF5688F3), Color(0xFFA879E3), Color(0xFFF0B63B), Color(0xFFF2787C))
private val RoleGreen = Color(0xFF10A987)
private val RolePurple = Color(0xFF8B5CC7)

@Composable
fun UsersScreen(
    state: UiState<UsersUiData>, query: String, onSearch: (String) -> Unit,
    onRefresh: () -> Unit, onBack: () -> Unit,
    onCreate: (CreateUserDto, (Result<Unit>) -> Unit) -> Unit, onUpdate: (Int, UpdateUserDto, (Result<Unit>) -> Unit) -> Unit,
    onToggleStatus: (Int, (Result<Unit>) -> Unit) -> Unit,
    onResetPassword: (Int, String, (Result<Unit>) -> Unit) -> Unit
) {
    var editing by remember { mutableStateOf<UserDto?>(null) }
    var creating by remember { mutableStateOf(false) }
    var resetting by remember { mutableStateOf<UserDto?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize().background(ScreenBackground)) {
        when (state) {
            UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryGreenButton) }
            is UiState.Error -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(state.message, color = TextMuted); Spacer(Modifier.height(10.dp)); Button(onClick = onRefresh) { Text("Reintentar") }
            }
            is UiState.Success -> {
                val users = state.data.users.filter { query.isBlank() || "${it.firstName} ${it.lastName} ${it.userName} ${it.email} ${it.roles.joinToString()}".contains(query, ignoreCase = true) }
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    item {
                        Row(Modifier.fillMaxWidth().height(38.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = TextDark) }
                                Text("Usuarios", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            }
                            IconButton(onClick = { editing = null; creating = true }, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryGreenButton)) {
                                Icon(Icons.Default.PersonAdd, "Agregar usuario", tint = Color.White, modifier = Modifier.size(19.dp))
                            }
                        }
                    }
                    item {
                        val active = state.data.users.count { it.isActive }
                        val admins = state.data.users.count { it.roles.any(::isAdminRole) }
                        val vendors = state.data.users.count { it.roles.any(::isVendorRole) }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            UserStatCard("Activos", active, KpiGreenBg, RoleGreen, Icons.Default.PeopleAlt, Modifier.weight(1f))
                            UserStatCard("Admins", admins, KpiYellowBg, Color(0xFFD99B16), Icons.Default.Shield, Modifier.weight(1f))
                            UserStatCard("Vendedores", vendors, TagReabastecimientoBg, RolePurple, Icons.Default.Storefront, Modifier.weight(1f))
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = query, onValueChange = onSearch, modifier = Modifier.fillMaxWidth().height(50.dp),
                            singleLine = true, shape = RoundedCornerShape(13.dp), placeholder = { Text("Buscar por nombre o usuario...", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = InputBackground, focusedContainerColor = CardBackground, unfocusedBorderColor = Color.Transparent, focusedBorderColor = PrimaryGreenButton),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                        )
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Lista de Usuarios", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text("${users.size}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryGreenButton, modifier = Modifier.clip(CircleShape).background(KpiGreenBg).padding(horizontal = 7.dp, vertical = 2.dp))
                        }
                    }
                    if (users.isEmpty()) item {
                        Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(if (query.isBlank()) "Aún no hay usuarios" else "No encontramos usuarios", fontWeight = FontWeight.SemiBold, color = TextDark)
                                Text("Los usuarios que agregues aparecerán aquí.", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }
                    items(users, key = { it.id }) { user ->
                        UserCard(user, onEdit = { editing = user }, onToggle = {
                            onToggleStatus(user.id) { result -> message = result.fold({ "Estado del usuario actualizado." }, { it.message ?: "No se pudo cambiar el estado." }) }
                        }, onReset = { resetting = user })
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
    if (creating || editing != null) UserFormDialog(editing, onDismiss = { creating = false; editing = null }, onSave = { userName, email, idNumber, firstName, lastName, password, roleName, active ->
        val current = editing
        if (current == null) onCreate(CreateUserDto(userName, email, idNumber, firstName, lastName, password, roleName)) { result -> message = result.fold({ "Usuario creado correctamente." }, { it.message ?: "No se pudo crear el usuario." }) }
        else onUpdate(current.id, UpdateUserDto(userName, email, idNumber, firstName, lastName, roleName, active)) { result -> message = result.fold({ "Usuario actualizado correctamente." }, { it.message ?: "No se pudo actualizar el usuario." }) }
        creating = false; editing = null
    })
    resetting?.let { user -> ResetPasswordDialog(user, onDismiss = { resetting = null }, onSave = { password ->
        onResetPassword(user.id, password) { result -> message = result.fold({ "Contraseña actualizada." }, { it.message ?: "No se pudo restablecer la contraseña." }) }
        resetting = null
    }) }
    message?.let { text -> AlertDialog(onDismissRequest = { message = null }, confirmButton = { TextButton(onClick = { message = null }) { Text("Aceptar") } }, text = { Text(text) }) }
}

@Composable
private fun UserStatCard(label: String, value: Int, background: Color, accent: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(modifier.height(58.dp), shape = RoundedCornerShape(13.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.fillMaxSize().padding(horizontal = 7.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(27.dp).clip(RoundedCornerShape(9.dp)).background(background), contentAlignment = Alignment.Center) { Icon(icon, null, tint = accent, modifier = Modifier.size(15.dp)) }
            Spacer(Modifier.width(6.dp)); Column {
                Text(label, fontSize = 8.sp, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(value.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }
        }
    }
}

@Composable
private fun UserCard(user: UserDto, onEdit: () -> Unit, onToggle: () -> Unit, onReset: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val initials = "${user.firstName.firstOrNull()?.uppercaseChar() ?: 'U'}${user.lastName.firstOrNull()?.uppercaseChar() ?: ' '}"
    val role = user.roles.firstOrNull().orEmpty()
    val roleColor = if (isAdminRole(role)) RoleGreen else RolePurple
    val roleBackground = if (isAdminRole(role)) KpiGreenBg else TagReabastecimientoBg
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(1.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(start = 9.dp, end = 4.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(UserAvatarColors[user.id.absoluteValue % UserAvatarColors.size]), contentAlignment = Alignment.Center) {
                Text(initials, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                Text("${user.firstName} ${user.lastName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("@ ${user.userName}", fontSize = 9.sp, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (!user.isActive) Text("INACTIVO", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC24148), modifier = Modifier.clip(RoundedCornerShape(7.dp)).background(KpiRedBg).padding(horizontal = 6.dp, vertical = 4.dp))
            else if (role.isNotBlank()) Text(role.uppercase(), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = roleColor, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.clip(RoundedCornerShape(7.dp)).background(roleBackground).padding(horizontal = 7.dp, vertical = 4.dp))
            Box {
                IconButton(onClick = { expanded = true }, modifier = Modifier.size(29.dp)) { Icon(Icons.Default.MoreVert, "Acciones", tint = TextMuted, modifier = Modifier.size(17.dp)) }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("Editar") }, onClick = { expanded = false; onEdit() })
                    DropdownMenuItem(text = { Text(if (user.isActive) "Desactivar" else "Activar") }, onClick = { expanded = false; onToggle() })
                    DropdownMenuItem(leadingIcon = { Icon(Icons.Default.LockReset, null) }, text = { Text("Restablecer contraseña") }, onClick = { expanded = false; onReset() })
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFB4BECD), modifier = Modifier.size(18.dp))
        }
    }
}

private fun isAdminRole(role: String) = role.contains("admin", ignoreCase = true)
private fun isVendorRole(role: String) = role.contains("vendedor", ignoreCase = true) || role.contains("seller", ignoreCase = true)

@Composable
private fun UserFormDialog(user: UserDto?, onDismiss: () -> Unit, onSave: (String, String, String, String, String, String, String, Boolean) -> Unit) {
    var userName by remember(user) { mutableStateOf(user?.userName.orEmpty()) }
    var email by remember(user) { mutableStateOf(user?.email.orEmpty()) }
    var idNumber by remember(user) { mutableStateOf(user?.idNumber.orEmpty()) }
    var first by remember(user) { mutableStateOf(user?.firstName.orEmpty()) }
    var last by remember(user) { mutableStateOf(user?.lastName.orEmpty()) }
    var password by remember(user) { mutableStateOf("") }
    var role by remember(user) { mutableStateOf(user?.roles?.firstOrNull() ?: "Administrador") }
    var active by remember(user) { mutableStateOf(user?.isActive ?: true) }
    var roleExpanded by remember { mutableStateOf(false) }
    val validId = Regex("^[0-9]{13}[a-zA-Z]$").matches(idNumber)
    val validPassword = Regex("^(?=.*[a-zA-Z])(?=.*\\d).{6,}$").matches(password)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (user == null) "Nuevo usuario" else "Editar usuario") },
        text = {
            Column(Modifier.heightIn(max = 440.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                OutlinedTextField(first, { first = it }, label = { Text("Nombre") }, singleLine = true)
                OutlinedTextField(last, { last = it }, label = { Text("Apellido") }, singleLine = true)
                OutlinedTextField(userName, { userName = it }, label = { Text("Nombre de usuario") }, singleLine = true)
                OutlinedTextField(email, { email = it }, label = { Text("Correo electrónico") }, singleLine = true)
                OutlinedTextField(idNumber, { idNumber = it }, label = { Text("Cédula (13 dígitos y letra)") }, singleLine = true)
                if (user == null) OutlinedTextField(password, { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), singleLine = true, supportingText = { Text("Al menos 6 caracteres, una letra y un número") })
                Box {
                    OutlinedTextField(role, {}, label = { Text("Rol") }, readOnly = true, singleLine = true, trailingIcon = { TextButton(onClick = { roleExpanded = true }) { Text("▾") } })
                    DropdownMenu(expanded = roleExpanded, onDismissRequest = { roleExpanded = false }) {
                        listOf("Administrador", "Gerente", "Vendedor").forEach { value -> DropdownMenuItem(text = { Text(value) }, onClick = { role = value; roleExpanded = false }) }
                    }
                }
                if (user != null) Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(active, { active = it }); Text("Activo") }
            }
        },
        confirmButton = {
            TextButton(enabled = first.isNotBlank() && last.isNotBlank() && userName.length in 3..50 && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && validId && (user != null || validPassword), onClick = {
                onSave(userName.trim(), email.trim(), idNumber.trim(), first.trim(), last.trim(), password, role, active)
            }) { Text("Guardar") }
        }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun ResetPasswordDialog(user: UserDto, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var password by remember(user.id) { mutableStateOf("") }
    var confirmation by remember(user.id) { mutableStateOf("") }
    val valid = Regex("^(?=.*[a-zA-Z])(?=.*\\d).{6,}$").matches(password)
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Restablecer contraseña") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Nueva contraseña para ${user.userName}.", fontSize = 12.sp, color = TextMuted)
            OutlinedTextField(password, { password = it }, label = { Text("Nueva contraseña") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
            OutlinedTextField(confirmation, { confirmation = it }, label = { Text("Confirmar contraseña") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
        } },
        confirmButton = { TextButton(enabled = valid && password == confirmation, onClick = { onSave(password) }) { Text("Actualizar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}
