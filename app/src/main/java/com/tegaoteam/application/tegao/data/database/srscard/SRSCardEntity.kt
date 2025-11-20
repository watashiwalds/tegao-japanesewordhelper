package com.tegaoteam.application.tegao.data.database.srscard

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tegaoteam.application.tegao.domain.model.CardEntry

@Entity(
    tableName = SRSCardEntity.TABLE_NAME,
    indices = [
        Index(value = [SRSCardEntity.COL_GROUPID])
    ]
)
data class SRSCardEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COL_ID) val cardId: Long,
    @ColumnInfo(name = COL_GROUPID) val groupId: Long,
    @ColumnInfo(name = COL_TYPE) val type: Int,
    @ColumnInfo(name = COL_FRONT) val front: String,
    @ColumnInfo(name = COL_ANSWER) val answer: String?,
    @ColumnInfo(name = COL_BACK) val back: String
) {
    companion object {
        const val TABLE_NAME = "srs_card_detail"
        const val COL_ID = "cardId"
        const val COL_GROUPID = SRSCardGroup.COL_ID
        const val COL_TYPE = "type"
        const val COL_FRONT = "front"
        const val COL_ANSWER = "answer"
        const val COL_BACK = "back"

        fun toDomainCardEntry(entity: SRSCardEntity) = CardEntry(
            entity.cardId,
            entity.groupId,
            entity.type,
            entity.front,
            entity.answer,
            entity.back
        )

        fun fromDomainCardEntry(entry: CardEntry) = SRSCardEntity(
            entry.cardId,
            entry.groupId,
            entry.type,
            entry.front,
            entry.answer,
            entry.back
        )
    }
}
