package com.tegaoteam.application.tegao.data.database.learningcard

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tegaoteam.application.tegao.domain.model.CardEntry

@Entity(
    tableName = CardEntity.TABLE_NAME,
    indices = [
        Index(value = [CardEntity.COL_GROUPID])
    ]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COL_ID) val cardId: Long,
    @ColumnInfo(name = COL_GROUPID) val groupId: Long,
    @ColumnInfo(name = COL_TYPE) val type: Int,
    @ColumnInfo(name = COL_DATECREATED, defaultValue = "2025-10-01T00:00:00Z") val dateCreated: String,
    @ColumnInfo(name = COL_FRONT) val front: String,
    @ColumnInfo(name = COL_ANSWER) val answer: String?,
    @ColumnInfo(name = COL_BACK) val back: String
) {
    companion object {
        const val TABLE_NAME = "learning_card_detail"
        const val COL_ID = "cardId"
        const val COL_GROUPID = CardGroupEntity.COL_ID
        const val COL_TYPE = "type"
        const val COL_DATECREATED = "dateCreated"
        const val COL_FRONT = "front"
        const val COL_ANSWER = "answer"
        const val COL_BACK = "back"

        fun toDomainCardEntry(entity: CardEntity?): CardEntry {
            if (entity != null) return CardEntry(
                entity.cardId,
                entity.groupId,
                entity.type,
                entity.front,
                entity.dateCreated,
                entity.answer,
                entity.back
            ) else return CardEntry.default()
        }

        fun fromDomainCardEntry(entry: CardEntry) = CardEntity(
            entry.cardId,
            entry.groupId,
            entry.type,
            entry.front,
            entry.dateCreated,
            entry.answer,
            entry.back
        )
    }
}
