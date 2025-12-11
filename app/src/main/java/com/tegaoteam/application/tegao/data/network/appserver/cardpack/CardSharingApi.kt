package com.tegaoteam.application.tegao.data.network.appserver.cardpack

import com.google.gson.JsonElement
import com.tegaoteam.application.tegao.data.database.cardpack.CardDeck
import com.tegaoteam.application.tegao.data.network.ErrorResults
import com.tegaoteam.application.tegao.data.network.RetrofitMaker
import com.tegaoteam.application.tegao.data.network.RetrofitResult
import com.tegaoteam.application.tegao.domain.independency.RepoResult
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
}