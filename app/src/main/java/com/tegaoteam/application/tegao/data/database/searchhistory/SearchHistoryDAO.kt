package com.tegaoteam.application.tegao.data.database.searchhistory

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.tegaoteam.application.tegao.domain.model.SearchHistory
import com.tegaoteam.application.tegao.data.database.searchhistory.SearchHistoryConst as Const

@Dao
interface SearchHistoryDAO {
    @Upsert fun upsert(entry: SearchHistoryEntity)

    @Query("select * from ${Const.TABLE_NAME} " +
            "where ${Const.COL_TYPE} = ${SearchHistory.TYPE_WORD} " +
            "order by ${Const.COL_SEARCHDATE} desc")
    fun getSearchedWords(): LiveData<List<SearchHistoryEntity>>

    @Query("select * from ${Const.TABLE_NAME} " +
            "where ${Const.COL_TYPE} = ${SearchHistory.TYPE_KANJI} " +
            "order by ${Const.COL_SEARCHDATE} desc")
    fun getSearchedKanjis(): LiveData<List<SearchHistoryEntity>>
}