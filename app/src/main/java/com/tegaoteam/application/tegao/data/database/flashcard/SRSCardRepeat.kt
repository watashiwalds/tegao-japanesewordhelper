package com.tegaoteam.application.tegao.data.database.flashcard

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = SRSCardRepeat.TABLE_NAME
)
data class SRSCardRepeat(
    @PrimaryKey @ColumnInfo(name = COL_ID) val cardId: Long,
    @ColumnInfo(name = COL_REPEATAT) var repeatAt: String
) {
    companion object {
        const val TABLE_NAME = "srs_card_repeat"
        const val COL_ID = SRSCardEntity.COL_ID
        const val COL_REPEATAT = "repeatAt"
    }
}
