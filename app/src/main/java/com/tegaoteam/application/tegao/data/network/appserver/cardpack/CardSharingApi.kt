package com.tegaoteam.application.tegao.data.network.appserver.cardpack

import com.google.gson.JsonElement
import com.tegaoteam.application.tegao.data.database.cardpack.CardDeck
import com.tegaoteam.application.tegao.data.model.FlowStream
import com.tegaoteam.application.tegao.data.utils.ErrorResults
import com.tegaoteam.application.tegao.data.network.RetrofitMaker
import com.tegaoteam.application.tegao.data.network.RetrofitResult
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.domain.model.CardEntry
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class CardSharingApi {
    private val retrofit by lazy { RetrofitMaker.createWithUrl("", RetrofitMaker.TYPE_BROWSING).create(CardSharingApiInterface::class.java) }

    suspend fun getCardpackContentByLink(link: String): RepoResult<List<CardDeck>> {
        if (link.isBlank()) return ErrorResults.RepoRes.LINK_ERROR
        val res = RetrofitResult.wrapper { retrofit.getJson(link) }
        return when (res) {
            is RepoResult.Error<*> -> RepoResult.Error<Nothing>(res.code, res.message)
            is RepoResult.Success<JsonElement> -> RepoResult.Success(CardSharingDataPasrer.getDecksFromCardPackJson(res.data))
        }
    }

    suspend fun getCarddeckContentByLink(link: String): FlowStream<RepoResult<CardDeck>> = FlowStream( flow {
        if (link.isBlank()) {
            emit(ErrorResults.RepoRes.LINK_ERROR)
            return@flow
        }

        val res = RetrofitResult.wrapper { retrofit.getJson(link) }
        when (res) {
            is RepoResult.Error<*> -> emit(RepoResult.Error<Nothing>(res.code, res.message))
            is RepoResult.Success<JsonElement> -> {
                val infoDeck = CardSharingDataPasrer.parseToInformationCardDeck(res.data)
                emit(RepoResult.Success(infoDeck))
                emit(RepoResult.Success(CardSharingDataPasrer.parseToCompletedCardDeck(res.data, infoDeck)))
            }
        }
    } )
}