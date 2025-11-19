package com.tegaoteam.application.tegao.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.data.database.dictionarycache.DictionaryCacheDAO
import com.tegaoteam.application.tegao.data.database.dictionarycache.DictionaryCacheEntity
import com.tegaoteam.application.tegao.data.database.flashcard.SRSCardDAO
import com.tegaoteam.application.tegao.data.database.flashcard.SRSCardEntity
import com.tegaoteam.application.tegao.data.database.flashcard.SRSCardGroup
import com.tegaoteam.application.tegao.data.database.flashcard.SRSCardRepeat
import com.tegaoteam.application.tegao.data.database.searchhistory.SearchHistoryEntity
import com.tegaoteam.application.tegao.data.database.searchhistory.SearchHistoryDAO

@Database(
    entities = [
        SearchHistoryEntity::class,
        DictionaryCacheEntity::class,
        SRSCardGroup::class,
        SRSCardEntity::class,
        SRSCardRepeat::class,
    ],
    version = SQLiteDatabase.Companion.DATABASE_VERSION,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 2, to = 4),
        AutoMigration(from = 3, to = 4),
                     ],
    exportSchema = true)
abstract class SQLiteDatabase: RoomDatabase() {

    abstract val searchHistoryDAO: SearchHistoryDAO
    abstract val dictionaryCacheDAO: DictionaryCacheDAO
    abstract val srsCardDAO: SRSCardDAO

    companion object {
        const val DATABASE_NAME = "tegao_sqlite_db"
        const val DATABASE_VERSION = 4

        @Volatile
        private var _instance: SQLiteDatabase? = null

        fun getInstance(context: Context = TegaoApplication.instance): SQLiteDatabase {
            synchronized(this) {
                var instance = _instance
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, SQLiteDatabase::class.java, DATABASE_NAME)
                        .addMigrations()
                        .fallbackToDestructiveMigration(true) //TODO: Change this to a migratable function to keep user data after upgrade
                        .build()
                    _instance = instance
                }
                return _instance!!
            }
        }
    }
}