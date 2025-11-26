package com.tegaoteam.application.tegao.domain.model

data class CardEntry(
    val cardId: Long,
    val groupId: Long,
    val type: Int,
    val dateCreated: String,
    val front: String,
    val answer: String?,
    val back: String
) {
    companion object {
        const val TYPE_FLASHCARD = 0
        const val TYPE_ANSWERCARD = 1

        fun default() = CardEntry(
            cardId = 0,
            groupId = 0,
            type = 0,
            dateCreated = "",
            front = "",
            answer = null,
            back = ""
        )
    }
}
