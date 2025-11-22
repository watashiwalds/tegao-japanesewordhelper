package com.tegaoteam.application.tegao.ui.learning.cardcreate.model

import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.ui.learning.LearningCardConst
import com.tegaoteam.application.tegao.utils.Time

data class CardPlaceholder(
    var cardId: Long? = null,
    var groupId: Long? = null,
    var type: Int? = null,
    var dateCreated: String? = null,
    var front: String? = null,
    var answer: String? = null,
    var back: String? = null
) {
    companion object {
        fun toDomainCardEntry(plc: CardPlaceholder) = CardEntry(
            plc.cardId?: 0,
            plc.groupId?: 0,
            plc.type?: LearningCardConst.Type.TYPE_FLASHCARD.id,
            plc.dateCreated?: Time.getCurrentTimestamp().toString(),
            plc.front?: "-",
            plc.answer,
            plc.back?: "-"
        )
    }
}
