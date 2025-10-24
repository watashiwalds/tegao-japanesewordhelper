package com.tegaoteam.application.tegao.data.database.searchhistory

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tegaoteam.application.tegao.data.database.searchhistory.SearchHistoryConst as Const

@Dao
interface SearchHistoryDAO {
    @Insert fun insert(entry: HistoryEntity)
    @Update fun update(entry: HistoryEntity)

    @Query("select * from ${Const.TABLE_NAME} " +
            "where ${Const.COL_TYPE} = ${Const.TYPE_WORD} " +
            "order by ${Const.COL_SEARCHDATE} desc")
    fun getWordSearches(): LiveData<List<HistoryEntity>>

    @Query("select * from ${Const.TABLE_NAME} " +
            "where ${Const.COL_TYPE} = ${Const.TYPE_KANJI} " +
            "order by ${Const.COL_SEARCHDATE} desc")
    fun getKanjiSearches(): LiveData<List<HistoryEntity>>
}