package com.tegaoteam.application.tegao.data.hub

import com.google.gson.stream.JsonWriter
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.data.database.cardpack.CardDeck
import com.tegaoteam.application.tegao.data.database.cardpack.CardPack
import com.tegaoteam.application.tegao.data.model.FlowStream
import com.tegaoteam.application.tegao.data.model.asFlow
import com.tegaoteam.application.tegao.data.network.appserver.cardpack.CardSharingApi
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.domain.repo.LearningRepo
import com.tegaoteam.application.tegao.utils.getStringFromAppRes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.File
import java.io.FileWriter

class SharingHub private constructor() {
    companion object {
        private val appContext = TegaoApplication.instance
        val instance by lazy { SharingHub() }

        private const val defaultCardpackLink = "https://raw.githubusercontent.com/watashiwalds/tegaores-contents/refs/heads/main/demo_cardPack/pack.json"
    }

    //region Json export and import of card deck
    suspend fun exportCardDeckToJsonString(learningRepo: LearningRepo, groupId: Long): String {
        val group = learningRepo.getCardGroupByGroupId(groupId).asFlow().first()
        val cards = learningRepo.getCardsByGroupId(groupId).asFlow().first()

        val res = File(appContext.getExternalFilesDir(null), "tempExport_cardDeck.json")
        val fileWriter = FileWriter(res)
        val jsonWriter = JsonWriter(fileWriter)
        jsonWriter.apply {
            beginObject()
                name("label").value(group.label)
                name("author").value(getStringFromAppRes(R.string.card_sharing_deckExport_defaultValue_author))
                name("description").value(getStringFromAppRes(R.string.card_sharing_deckExport_defaultValue_description))
                name("cards")
                beginArray()
                    cards.forEach { c ->
                        beginObject()
                        name("type").value(if (c.type == CardEntry.TYPE_ANSWERCARD) "answer" else "flash")
                        name("front").value(c.front)
                        name("back").value(c.back)
                        if (c.type == CardEntry.TYPE_ANSWERCARD) name("answer").value(c.answer?: "")
                        endObject()
                    }
                endArray()
            endObject()
        }
        jsonWriter.close()
        fileWriter.close()

        return res.readText()
    }
    //endregion

    //region Related functions that serves card pack data retrieve
    private val cardSharingApi = CardSharingApi()

        //todo: Make a proper sql table to store added cardpack sources
    fun getSavedCardpackSources() = listOf(
        CardPack(
            id = 0,
            packName = "TegaoTeam Demo Cardpack",
            link = defaultCardpackLink
        )
    )

    suspend fun getCardpackContents(packLink: String) = FlowStream( flow {
        //todo: Fetch the sql for cached version first
        val res = cardSharingApi.getCardpackContentByLink(packLink)
        emit(res)
    } )

    suspend fun getCarddeckContent(deckLink: String) = cardSharingApi.getCarddeckContentByLink(deckLink)
    //endregion
}