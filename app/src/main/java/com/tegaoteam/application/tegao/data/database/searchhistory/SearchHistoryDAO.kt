package com.tegaoteam.application.tegao.data.database.searchhistory

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.tegaoteam.application.tegao.domain.model.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDAO {
    @Upsert suspend fun upsert(entry: SearchHistoryEntity)

    @Query("select * from ${SearchHistoryEntity.TABLE_NAME} " +
            "where ${SearchHistoryEntity.COL_TYPE} = ${SearchHistory.TYPE_WORD} " +
            "order by ${SearchHistoryEntity.COL_SEARCHDATE} desc")
    fun getSearchedWords(): Flow<List<SearchHistoryEntity>>

    @Query("select * from ${SearchHistoryEntity.TABLE_NAME} " +
            "where ${SearchHistoryEntity.COL_TYPE} = ${SearchHistory.TYPE_KANJI} " +
            "order by ${SearchHistoryEntity.COL_SEARCHDATE} desc")
    fun getSearchedKanjis(): Flow<List<SearchHistoryEntity>>
}