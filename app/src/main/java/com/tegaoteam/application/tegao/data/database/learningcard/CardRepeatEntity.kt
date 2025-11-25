package com.tegaoteam.application.tegao.data.database.learningcard

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tegaoteam.application.tegao.domain.model.CardRepeat
import com.tegaoteam.application.tegao.utils.Time

@Entity(
    tableName = CardRepeatEntity.TABLE_NAME
)
data class CardRepeatEntity(
    @PrimaryKey @ColumnInfo(name = COL_ID) val cardId: Long,
    @ColumnInfo(name = COL_LASTREPEAT) var lastRepeat: String = Time.getTodayMidnightTimestamp().toString(),
    @ColumnInfo(name = COL_NEXTREPEAT) var nextRepeat: String = Time.getTodayMidnightTimestamp().toString()
) {
    companion object {
        const val TABLE_NAME = "learning_card_repeat"
        const val COL_ID = CardEntity.COL_ID
        const val COL_LASTREPEAT = "lastRepeat"
        const val COL_NEXTREPEAT = "nextRepeat"

        fun toDomainCardRepeat(entity: CardRepeatEntity) = CardRepeat(
            entity.cardId,
            entity.lastRepeat,
            entity.nextRepeat
        )

        fun fromDomainCardRepeat(entry: CardRepeat) = CardRepeatEntity(
            entry.cardId,
            entry.lastRepeat,
            entry.nextRepeat
        )
    }
}
