package com.tegaoteam.application.tegao.data.database.srscard

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tegaoteam.application.tegao.domain.model.CardGroup

@Entity(
    tableName = SRSCardGroup.TABLE_NAME
)
data class SRSCardGroup(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COL_ID) val groupId: Long,
    @ColumnInfo(name = COL_LABEL) val label: String
) {
    companion object {
        const val TABLE_NAME = "srs_card_group"
        const val COL_ID = "groupId"
        const val COL_LABEL = "label"

        fun toDomainCardGroup(entity: SRSCardGroup) = CardGroup(
            entity.groupId,
            entity.label
        )

        fun fromDomainCardGroup(entry: CardGroup) = SRSCardGroup(
            entry.groupId,
            entry.label
        )
    }
}
