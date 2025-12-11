package com.tegaoteam.application.tegao.data.network.appserver.cardpack

import com.google.gson.JsonElement
import com.tegaoteam.application.tegao.data.database.cardpack.CardDeck
import timber.log.Timber

object CardSharingDataPasrer {
    fun getDecksFromCardPackJson(cardPackData: JsonElement): List<CardDeck> {
        val res = mutableListOf<CardDeck>()
        val decks = cardPackData.takeIf { it.isJsonObject }?.asJsonObject!!.get("decks")?.takeIf { it.isJsonArray }?.asJsonArray!!
        decks.forEach { deck ->
            val pDeck = deck.takeIf { it.isJsonObject }?.asJsonObject!!
            res.add(CardDeck(
                id = 0,
                packId = 0,
                label = pDeck.get("label")?.asString?: "",
                author = pDeck.get("author")?.asString?: "",
                description = pDeck.get("description")?.asString?: "",
                link = pDeck.get("link")?.asString?: ""
            ))
        }
        return res
    }
}