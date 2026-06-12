package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.DesignProject

@Database(entities = [DesignProject::class], version = 1, exportSchema = false)
abstract class DesignDatabase : RoomDatabase() {
    abstract fun designDao(): DesignDao

    companion object {
        @Volatile
        private var INSTANCE: DesignDatabase? = null

        fun getDatabase(context: Context): DesignDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DesignDatabase::class.java,
                    "yafta_design_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
