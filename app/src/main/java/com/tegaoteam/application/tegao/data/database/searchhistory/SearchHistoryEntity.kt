package com.tegaoteam.application.tegao.data.database.searchhistory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tegaoteam.application.tegao.data.database.searchhistory.SearchHistoryConst as Const

@Entity(tableName = Const.TABLE_NAME)
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = Const.COL_TYPE) val type: Int,
    @ColumnInfo(name = Const.COL_KEYWORD) val keyword: String,
    @ColumnInfo(name = Const.COL_SEARCHDATE) val searchDate: String
)