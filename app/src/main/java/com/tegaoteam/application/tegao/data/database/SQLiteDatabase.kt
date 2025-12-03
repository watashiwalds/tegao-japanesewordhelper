package com.tegaoteam.application.tegao.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameTable
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.data.database.dictionarycache.DictionaryCacheDAO
import com.tegaoteam.application.tegao.data.database.dictionarycache.DictionaryCacheEntity
import com.tegaoteam.application.tegao.data.database.learningcard.LearningCardDAO
import com.tegaoteam.application.tegao.data.database.learningcard.CardEntity
import com.tegaoteam.application.tegao.data.database.learningcard.CardGroupEntity
import com.tegaoteam.application.tegao.data.database.learningcard.CardRepeatEntity
import com.tegaoteam.application.tegao.data.database.searchhistory.SearchHistoryEntity
import com.tegaoteam.application.tegao.data.database.searchhistory.SearchHistoryDAO

@Database(
    entities = [
        SearchHistoryEntity::class,
        DictionaryCacheEntity::class,
        CardGroupEntity::class,
        CardEntity::class,
        CardRepeatEntity::class,
    ],
    version = SQLiteDatabase.Companion.DATABASE_VERSION,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7, spec = SQLiteDatabase.MigrationSpec6To7::class),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9)
                     ],
    exportSchema = true)
abstract class SQLiteDatabase: RoomDatabase() {

    abstract val searchHistoryDAO: SearchHistoryDAO
    abstract val dictionaryCacheDAO: DictionaryCacheDAO
    abstract val learningCardDAO: LearningCardDAO

    companion object {
        const val DATABASE_NAME = "tegao_sqlite_db"
        const val DATABASE_VERSION = 9

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

    @RenameTable(fromTableName = "srs_card_detail", toTableName = "learning_card_detail")
    @RenameTable(fromTableName = "srs_card_group", toTableName = "learning_card_group")
    @RenameTable(fromTableName = "srs_card_repeat", toTableName = "learning_card_repeat")
    class MigrationSpec6To7: AutoMigrationSpec
}