package com.tegaoteam.application.tegao.data.database.searchhistory

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.tegaoteam.application.tegao.domain.model.SearchHistory
import kotlinx.coroutines.flow.Flow
import com.tegaoteam.application.tegao.data.database.searchhistory.SearchHistoryConst as Const

@Dao
interface SearchHistoryDAO {
    @Upsert fun upsert(entry: SearchHistoryEntity)

    @Query("select * from ${Const.TABLE_NAME} " +
            "where ${Const.COL_TYPE} = ${SearchHistory.TYPE_WORD} " +
            "order by ${Const.COL_SEARCHDATE} desc")
    fun getSearchedWords(): Flow<List<SearchHistoryEntity>>

    @Query("select * from ${Const.TABLE_NAME} " +
            "where ${Const.COL_TYPE} = ${SearchHistory.TYPE_KANJI} " +
            "order by ${Const.COL_SEARCHDATE} desc")
    fun getSearchedKanjis(): Flow<List<SearchHistoryEntity>>
}