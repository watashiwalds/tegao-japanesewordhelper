package com.tegaoteam.application.tegao.data.database.cardpack

import com.tegaoteam.application.tegao.domain.model.CardEntry

data class CardDeck(
    val id: Long,
    val packId: Long,
    val link: String,
    val label: String,
    val author: String,
    val description: String
) {
    var parsedCards: List<CardEntry>? = null
        private set
    fun setParsedCards(list: List<CardEntry>) { parsedCards = list }
}