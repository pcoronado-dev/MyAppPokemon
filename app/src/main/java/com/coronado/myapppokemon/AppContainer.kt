package com.coronado.myapppokemon

import android.content.Context
import androidx.room.Room
import com.coronado.myapppokemon.data.auth.AuthRepository
import com.coronado.myapppokemon.data.auth.FirebaseAuthRepository
import com.coronado.myapppokemon.data.local.AppDatabase
import com.coronado.myapppokemon.data.remote.RetrofitClient
import com.coronado.myapppokemon.data.repository.ItemRepository
import com.coronado.myapppokemon.data.repository.ItemRepositoryImpl
import com.coronado.myapppokemon.domain.ConfigTema
import com.coronado.myapppokemon.notification.Notificador
import com.coronado.myapppokemon.notification.NotificadorAndroid

/** Crea la API, la base de datos y los repositorios. Los ViewModel lo reciben por su Factory. */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    private val preferencias by lazy {
        appContext.getSharedPreferences("config", Context.MODE_PRIVATE)
    }

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(appContext, AppDatabase::class.java, "pokemon.db")
            .fallbackToDestructiveMigration() // la versión 2 agrega tablas/columnas: se recrea el caché
            .build()
    }

    val authRepository: AuthRepository by lazy { FirebaseAuthRepository() }

    val itemRepository: ItemRepository by lazy {
        ItemRepositoryImpl(
            api = RetrofitClient.api,
            dao = database.itemDao(),
            // Se recuerda el último tipo elegido al cerrar y abrir la app
            tipoInicial = preferencias.getString("tipo", ConfigTema.TIPO_VARIANTE) ?: ConfigTema.TIPO_VARIANTE,
            alCambiarTipo = { tipo -> preferencias.edit().putString("tipo", tipo).apply() }
        )
    }

    val notificador: Notificador by lazy { NotificadorAndroid(appContext) }
}
