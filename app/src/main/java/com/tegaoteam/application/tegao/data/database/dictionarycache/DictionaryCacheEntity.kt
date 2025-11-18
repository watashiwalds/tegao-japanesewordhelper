package com.tegaoteam.application.tegao.data.database.dictionarycache

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = DictionaryCacheEntity.TABLE_NAME,
    primaryKeys = [DictionaryCacheEntity.COL_KEYWORD, DictionaryCacheEntity.COL_TYPE, DictionaryCacheEntity.COL_DICTIONARY]
)
data class DictionaryCacheEntity(
    @ColumnInfo(name = COL_KEYWORD) val keyword: String,
    @ColumnInfo(name = COL_TYPE) val type: Int,
    @ColumnInfo(name = COL_DICTIONARY) val dictionary: String,
    @ColumnInfo(name = COL_CACHEDATE) val cacheDate: String,
    @ColumnInfo(name = COL_JSONMD5) val jsonMD5: String,
    @ColumnInfo(name = COL_JSONTEXT) val json: String
) {
    companion object {
        const val TABLE_NAME = "dictionary_caches"
        const val COL_KEYWORD = "keyword"
        const val COL_TYPE = "type"
        const val COL_DICTIONARY = "dictId"
        const val COL_CACHEDATE = "cacheDate"
        const val COL_JSONMD5 = "md5"
        const val COL_JSONTEXT = "json"
    }
}
