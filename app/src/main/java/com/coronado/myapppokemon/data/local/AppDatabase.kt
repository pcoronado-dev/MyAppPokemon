package com.coronado.myapppokemon.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ItemEntity::class, ItemTipoEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}