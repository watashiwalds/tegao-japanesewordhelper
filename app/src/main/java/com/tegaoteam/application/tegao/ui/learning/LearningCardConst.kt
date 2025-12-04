package com.tegaoteam.application.tegao.ui.learning

import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.utils.getStringFromAppRes as getString

object LearningCardConst {
    enum class Type(val id: Int, val display: String, val iconResId: Int) {
        TYPE_FLASHCARD(
            CardEntry.TYPE_FLASHCARD,
            "${getString(R.string.card_type_flashcard_name)}\n${getString(R.string.card_type_flashcard_detail)}",
            R.drawable.ftc_round_flash_16),
        TYPE_ANSWERCARD(
            CardEntry.TYPE_ANSWERCARD,
            "${getString(R.string.card_type_answercard_name)}\n${getString(R.string.card_type_answercard_detail)}",
            R.drawable.ftc_round_input_16),
    }
}