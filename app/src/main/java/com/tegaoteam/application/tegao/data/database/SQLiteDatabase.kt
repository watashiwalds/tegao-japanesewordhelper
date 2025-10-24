package com.tegaoteam.application.tegao.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tegaoteam.application.tegao.data.database.searchhistory.HistoryEntity
import com.tegaoteam.application.tegao.data.database.searchhistory.SearchHistoryDAO
import com.tegaoteam.application.tegao.data.database.DatabaseConst as Const

@Database(entities = [HistoryEntity::class], version = Const.DATABASE_VERSION, exportSchema = false)
abstract class SQLiteDatabase: RoomDatabase() {

    abstract val searchHistoryDAO: SearchHistoryDAO

    companion object {
        @Volatile
        private var _instance: SQLiteDatabase? = null

        fun getInstance(context: Context): SQLiteDatabase {
            synchronized(this) {
                var instance = _instance
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, SQLiteDatabase::class.java, Const.DATABASE_NAME)
                        .fallbackToDestructiveMigration(true) //TODO: Change this to a migratable function to keep user data after upgrade
                        .build()
                    _instance = instance
                }
                return _instance!!
            }
        }
    }
}