package com.tegaoteam.application.tegao.ui.learning

import com.tegaoteam.application.tegao.domain.model.CardEntry

object LearningCardConst {
    enum class Type(id: Int, display: String) {
        TYPE_FLASHCARD(CardEntry.TYPE_FLASHCARD, "Thẻ xem nhanh"),
        TYPE_ANSWERCARD(CardEntry.TYPE_ANSWERCARD, "Thẻ trả lời")
    }
}