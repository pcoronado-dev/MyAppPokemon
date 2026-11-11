package com.coronado.myapppokemon.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coronado.myapppokemon.ui.UiState
import com.coronado.myapppokemon.ui.components.Pokebola

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onIngreso: () -> Unit,
    onIrARegistro: () -> Unit
) {
    val sesion by viewModel.sesion.collectAsStateWithLifecycle()
    val form by viewModel.formulario.collectAsStateWithLifecycle()

    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Cuando el ViewModel confirma la sesión, se avisa para navegar
    LaunchedEffect(sesion) {
        val s = sesion
        if (s is UiState.Exito && s.datos != null) onIngreso()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE3F2FD), // Light blue top
                        Color(0xFFFFFFFF), // White bottom
                        Color(0xFFF5F9FF)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Pokébola prominente en la parte superior central
            Pokebola(tamano = 120.dp)

            Spacer(modifier = Modifier.height(16.dp))

            // Título grande en texto rojo "MyAppPokemon"
            Text(
                text = "MyAppPokemon",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = Color(0xFFEE1515)
            )

            // Subtítulo
            Text(
                text = "Ingresa para ver la Pokédex",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF555555),
                modifier = Modifier.padding(bottom = 32.dp, top = 4.dp)
            )

            // Campo de texto superior para "Correo" con icono de sobre y borde brillante cian neón
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Icono Correo",
                        tint = Color(0xFF00BCD4)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00E5FF),
                    unfocusedBorderColor = Color(0xFFB0BEC5),
                    focusedLabelColor = Color(0xFF00BCD4),
                    cursorColor = Color(0xFF00E5FF),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF00E5FF), Color(0xFF00838F))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de texto inferior para "Contraseña" con icono de candado y opción de ocultar/mostrar
            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Icono Contraseña",
                        tint = Color(0xFF546E7A)
                    )
                },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description, tint = Color(0xFF78909C))
                    }
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00E5FF),
                    unfocusedBorderColor = Color(0xFFB0BEC5),
                    focusedLabelColor = Color(0xFF00BCD4),
                    cursorColor = Color(0xFF00E5FF),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            form.mensaje?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            form.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de acción redondeado en color naranja rojizo brillante con el texto "Ingresar"
            Button(
                onClick = { viewModel.iniciarSesion(correo, clave) },
                enabled = !form.cargando,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5722)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                if (form.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = "Ingresar",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Texto inferior "¿No tienes cuenta? Regístrate" con "Regístrate" en azul oscuro y subrayado
            TextButton(onClick = onIrARegistro, enabled = !form.cargando) {
                Text(
                    text = buildAnnotatedString {
                        append("¿No tienes cuenta? ")
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF0D47A1),
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Regístrate")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
