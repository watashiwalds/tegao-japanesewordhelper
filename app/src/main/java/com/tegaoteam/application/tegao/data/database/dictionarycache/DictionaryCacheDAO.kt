package com.tegaoteam.application.tegao.data.database.dictionarycache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.tegaoteam.application.tegao.data.database.dictionarycache.DictionaryCacheEntity as Entity

@Dao
interface DictionaryCacheDAO {
    @Query("""
        select * from ${Entity.TABLE_NAME} 
        where ${Entity.COL_KEYWORD} = :keyword and ${Entity.COL_TYPE} = :type and ${Entity.COL_DICTIONARY} = :dictId
    """)
    fun getCache(keyword: String, type: Int, dictId: String): Entity?

    @Upsert
    suspend fun upsertCache(entity: Entity): Long
}