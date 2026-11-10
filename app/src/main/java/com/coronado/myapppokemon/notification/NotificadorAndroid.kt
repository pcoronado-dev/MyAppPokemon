package com.coronado.myapppokemon.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.coronado.myapppokemon.MainActivity
import com.coronado.myapppokemon.R

class NotificadorAndroid(private val context: Context) : Notificador {

    companion object {
        const val CANAL_ID = "sincronizacion"
        private const val NOTIFICACION_ID = 1
    }

    init {
        crearCanal()
    }

    private fun crearCanal() {
        // minSdk 26: los canales siempre existen
        val canal = NotificationChannel(
            CANAL_ID,
            "Sincronización",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Avisos cuando se terminan de guardar los Pokémon" }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(canal)
    }

    @SuppressLint("MissingPermission")
    override fun mostrar(titulo: String, texto: String) {
        // Si el permiso fue negado, la app sigue normal pero sin avisos (PA-14)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICACION_ID, notificacion)
    }
}
