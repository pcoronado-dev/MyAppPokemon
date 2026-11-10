package com.coronado.myapppokemon

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.coronado.myapppokemon.ui.navigation.AppNav
import com.coronado.myapppokemon.ui.theme.MyAppPokemonTheme

class MainActivity : ComponentActivity() {

    // Si la persona niega el permiso, la app funciona igual (sin avisos)
    private val pedirPermisoNotificaciones =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) solicitarPermisoNotificaciones()

        val container = (application as PokemonApp).container
        setContent {
            MyAppPokemonTheme {
                Surface(Modifier.fillMaxSize().safeDrawingPadding()) {
                    AppNav(container)
                }
            }
        }
    }

    private fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            pedirPermisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}