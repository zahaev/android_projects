// com/example/myapplication/model/local/AppDatabase.kt
package com.example.myapplication.model.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplication.model.data.local.dao.CharacterDao
import com.example.myapplication.model.data.local.entity.CharacterEntity
@Database(
    entities = [CharacterEntity::class,
        MetadataEntity::class],
    version = 4, exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun characterDao(): CharacterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "character_database_v3")
                    .fallbackToDestructiveMigration()// снос бд при ее изменении
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
