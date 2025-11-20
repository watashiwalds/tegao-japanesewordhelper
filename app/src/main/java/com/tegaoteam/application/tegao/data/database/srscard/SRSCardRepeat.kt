package com.tegaoteam.application.tegao.data.database.srscard

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tegaoteam.application.tegao.domain.model.CardRepeat

@Entity(
    tableName = SRSCardRepeat.TABLE_NAME
)
data class SRSCardRepeat(
    @PrimaryKey @ColumnInfo(name = COL_ID) val cardId: Long,
    @ColumnInfo(name = COL_LASTREPEAT) var lastRepeat: String,
    @ColumnInfo(name = COL_NEXTREPEAT) var nextRepeat: String
) {
    companion object {
        const val TABLE_NAME = "srs_card_repeat"
        const val COL_ID = SRSCardEntity.COL_ID
        const val COL_LASTREPEAT = "lastRepeat"
        const val COL_NEXTREPEAT = "nextRepeat"

        fun toDomainCardRepeat(entity: SRSCardRepeat) = CardRepeat(
            entity.cardId,
            entity.lastRepeat,
            entity.nextRepeat
        )

        fun fromDomainCardRepeat(entry: CardRepeat) = SRSCardRepeat(
            entry.cardId,
            entry.lastRepeat,
            entry.nextRepeat
        )
    }
}
