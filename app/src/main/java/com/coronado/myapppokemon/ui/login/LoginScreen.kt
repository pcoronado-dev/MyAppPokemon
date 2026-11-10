package com.coronado.myapppokemon.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
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

    // Cuando el ViewModel confirma la sesión, se avisa para navegar
    LaunchedEffect(sesion) {
        val s = sesion
        if (s is UiState.Exito && s.datos != null) onIngreso()
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Pokebola(tamano = 110.dp)
        Text(
            "MyAppPokemon",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(
            "Ingresa para ver la Pokédex",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = clave,
            onValueChange = { clave = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        form.mensaje?.let {
            Text(it, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
        }
        form.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        Button(
            onClick = { viewModel.iniciarSesion(correo, clave) },
            enabled = !form.cargando,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            if (form.cargando) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            else Text("Ingresar")
        }

        TextButton(onClick = onIrARegistro, enabled = !form.cargando) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}