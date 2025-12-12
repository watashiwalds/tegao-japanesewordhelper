package com.tegaoteam.application.tegao.data.network.appserver.cardpack

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.tegaoteam.application.tegao.data.database.cardpack.CardDeck
import com.tegaoteam.application.tegao.domain.model.CardEntry
import timber.log.Timber

object CardSharingDataPasrer {
    fun getDecksFromCardPackJson(cardPackData: JsonElement): List<CardDeck> {
        val res = mutableListOf<CardDeck>()
        val decks = cardPackData.takeIf { it.isJsonObject }?.asJsonObject!!.get("decks")?.takeIf { it.isJsonArray }?.asJsonArray!!
        decks.forEach { deck ->
            val pDeck = deck.takeIf { it.isJsonObject }?.asJsonObject!!
            res.add(CardDeck(
                id = 0,
                packId = -1,
                label = pDeck.get("label")?.asString?: "",
                author = pDeck.get("author")?.asString?: "",
                description = pDeck.get("description")?.asString?: "",
                link = pDeck.get("link")?.asString?: ""
            ))
        }
        return res
    }

    fun parseToInformationCardDeck(rawJson: String): CardDeck {
        val rawJElem = JsonParser.parseString(rawJson)
        return parseToInformationCardDeck(rawJElem)
    }
    fun parseToInformationCardDeck(deckData: JsonElement): CardDeck {
        val pDeck = deckData.takeIf { it.isJsonObject }?.asJsonObject!!
        val res = CardDeck(
            id = 0,
            packId = -1,
            label = pDeck.get("label")?.asString?: "",
            author = pDeck.get("author")?.asString?: "",
            description = pDeck.get("description")?.asString?: "",
            link = pDeck.get("link")?.asString?: ""
        )
        return res
    }

    fun parseToCompletedCardDeck(rawJson: String, infoDeck: CardDeck? = null): CardDeck {
        val rawJElem = JsonParser.parseString(rawJson)
        return parseToCompletedCardDeck(rawJElem, infoDeck)
    }
    fun parseToCompletedCardDeck(deckData: JsonElement, infoDeck: CardDeck? = null): CardDeck {
        val pDeck = deckData.takeIf { it.isJsonObject }?.asJsonObject!!
        val pCards = pDeck.get("cards").takeIf { it.isJsonArray }?.asJsonArray!!

        val res = (infoDeck?: parseToInformationCardDeck(deckData)).apply { setParsedCards(jsonArrayToListCards(pCards)) }
        return res
    }

    private fun jsonArrayToListCards(pCards: JsonArray?): List<CardEntry> {
        val cards = mutableListOf<CardEntry>()
        if (pCards == null) return cards

        pCards.forEach { card ->
            val pCard = card.takeIf { it.isJsonObject }?.asJsonObject!!
            val tType = when (pCard.get("type")?.asString) {
                "answer" -> CardEntry.TYPE_ANSWERCARD
                else -> CardEntry.TYPE_FLASHCARD
            }
            val tFront = pCard.get("front")?.asString
            val tBack = pCard.get("back")?.asString
            val tAnswer = if (tType == CardEntry.TYPE_ANSWERCARD) pCard.get("answer")?.asString else null

            if ((tFront?.isNotBlank()?: false) && (tBack?.isNotBlank()?: false)) {
                cards.add( CardEntry(
                    cardId = 0,
                    groupId = -1,
                    type = tType,
                    dateCreated = "",
                    front = tFront,
                    answer = tAnswer,
                    back = tBack
                ) )
            }
        }
        return cards
    }
}