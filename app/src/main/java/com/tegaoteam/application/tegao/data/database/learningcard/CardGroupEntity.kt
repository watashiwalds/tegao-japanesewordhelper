package com.tegaoteam.application.tegao.data.database.learningcard

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tegaoteam.application.tegao.domain.model.CardGroup

@Entity(
    tableName = CardGroupEntity.TABLE_NAME
)
data class CardGroupEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COL_ID) val groupId: Long,
    @ColumnInfo(name = COL_LABEL) val label: String
) {
    companion object {
        const val TABLE_NAME = "learning_card_group"
        const val COL_ID = "groupId"
        const val COL_LABEL = "label"

        fun toDomainCardGroup(entity: CardGroupEntity) = CardGroup(
            entity.groupId,
            entity.label
        )

        fun fromDomainCardGroup(entry: CardGroup) = CardGroupEntity(
            entry.groupId,
            entry.label
        )
    }
}
