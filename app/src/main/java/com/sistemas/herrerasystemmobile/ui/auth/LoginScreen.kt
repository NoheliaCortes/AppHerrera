package com.sistemas.herrerasystemmobile.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.sistemas.herrerasystemmobile.ui.common.UiState
import com.sistemas.herrerasystemmobile.ui.theme.*

@Composable
fun LoginScreen(
    loginState: UiState<String> = UiState.Success(""),
    onLoginClick: (String, String) -> Unit = { _, _ -> }
) {
    var usuario by remember { mutableStateOf("Marcelo") }
    var password by remember { mutableStateOf("herrerra") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        // Cabecera Verde Menta Curva (Sorbetería Herrera)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.42f)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(HeaderGradientStart, HeaderGradientEnd)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                // Insignia "SISTEMA DE GESTIÓN"
                Surface(
                    color = Color.White.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "SISTEMA DE GESTIÓN",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Isotipo / Logo de Herrera
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {

                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Sorbetería Herrera",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Accede al panel de control para administrar\nclientes, productos, sabores y más.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        // Formulario Inferior
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.58f)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Text(
                    text = "Ingresa tus credenciales para continuar.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Campo Usuario
                Text(text = "Usuario", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = usuario,
                    onValueChange = { usuario = it },
                    placeholder = { Text("Ej: Marcelo") },
                    leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = TextMuted) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBackground,
                        unfocusedContainerColor = InputBackground,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Contraseña
                Text(text = "Contraseña", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = TextMuted) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = null,
                                tint = TextMuted
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBackground,
                        unfocusedContainerColor = InputBackground,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Botón Ingresar
                Button(
                    onClick = { onLoginClick(usuario, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreenButton),
                    enabled = loginState !is UiState.Loading
                ) {
                    if (loginState is UiState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Ingresar al sistema", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                if (loginState is UiState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = loginState.message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Pie de Página
            Text(
                text = "© 2026 Sorbetería Herrera.\nTodos los derechos reservados.",
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Vista Previa Interactiva en Android Studio
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    HerreraSystemMobileTheme {
        LoginScreen()
    }
}