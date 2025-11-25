package com.tegaoteam.application.tegao.domain.model

data class CardRepeat(
    val cardId: Long,
    var lastRepeat: String,
    var nextRepeat: String?
)
