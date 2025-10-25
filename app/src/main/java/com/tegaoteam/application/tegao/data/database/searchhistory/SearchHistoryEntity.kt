package com.tegaoteam.application.tegao.data.database.searchhistory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tegaoteam.application.tegao.data.database.searchhistory.SearchHistoryEntity.Companion
import com.tegaoteam.application.tegao.domain.model.SearchHistory

@Entity(tableName = Companion.TABLE_NAME)
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = COL_TYPE) val type: Int,
    @ColumnInfo(name = COL_KEYWORD) val keyword: String,
    @ColumnInfo(name = COL_SEARCHDATE) val searchDate: String
) {
    fun toDomainSearchHistory() = SearchHistory(type, keyword, searchDate)
    companion object {
        fun fromDomainSearchHistory(entry: SearchHistory) = SearchHistoryEntity(0, entry.type, entry.keyword, entry.searchDate)

        const val TABLE_NAME = "search_histories"
        const val COL_TYPE = "type"
        const val COL_KEYWORD = "keyword"
        const val COL_SEARCHDATE = "searchDate"
    }
}