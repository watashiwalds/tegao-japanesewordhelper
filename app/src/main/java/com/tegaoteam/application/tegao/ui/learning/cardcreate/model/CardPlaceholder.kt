package com.tegaoteam.application.tegao.ui.learning.cardcreate.model

import com.tegaoteam.application.tegao.domain.model.CardEntry

data class CardPlaceholder(
    var groupId: Long = -1,
    var type: Int = -1,
    var front: String = "",
    var answer: String? = null,
    var back: String = ""
) {
    companion object {
        fun toDomainCardEntry(plc: CardPlaceholder) = CardEntry(
            0,
            plc.groupId,
            plc.type,
            plc.front,
            plc.answer,
            plc.back
        )
    }
}
