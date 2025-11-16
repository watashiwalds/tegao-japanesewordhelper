package com.tegaoteam.application.tegao.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tegaoteam.application.tegao.data.database.searchhistory.SearchHistoryEntity
import com.tegaoteam.application.tegao.data.database.searchhistory.SearchHistoryDAO

@Database(entities = [SearchHistoryEntity::class], version = SQLiteDatabase.Companion.DATABASE_VERSION, exportSchema = false)
abstract class SQLiteDatabase: RoomDatabase() {

    abstract val searchHistoryDAO: SearchHistoryDAO

    companion object {
        const val DATABASE_NAME = "tegao_sqlite_db"
        const val DATABASE_VERSION = 2

        @Volatile
        private var _instance: SQLiteDatabase? = null

        fun getInstance(context: Context): SQLiteDatabase {
            synchronized(this) {
                var instance = _instance
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, SQLiteDatabase::class.java, DATABASE_NAME)
                        .fallbackToDestructiveMigration(true) //TODO: Change this to a migratable function to keep user data after upgrade
                        .build()
                    _instance = instance
                }
                return _instance!!
            }
        }
    }
}