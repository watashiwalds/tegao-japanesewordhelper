package com.tegaoteam.application.tegao.data.database.cardpack

data class CardDeck(
    val id: Long,
    val packId: Long,
    val label: String,
    val author: String,
    val description: String,
    val link: String,
    val cached: String? = null
)