package com.tegaoteam.application.tegao.ui.learning.cardsharing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.database.cardpack.CardDeck
import com.tegaoteam.application.tegao.data.database.cardpack.CardPack
import com.tegaoteam.application.tegao.data.hub.SharingHub
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.utils.EventBeacon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CardSharingViewModel(private val _sharingHub: SharingHub): ViewModel() {
    val evFetchFailed = EventBeacon()
    val packSources = _sharingHub.getSavedCardpackSources()

    //region Card pack fetching (get deck list)
    private val _fetchedPack = MutableLiveData<List<CardDeck>>()
    val fetchedPack: LiveData<List<CardDeck>> = _fetchedPack
    fun fetchCardpackContents(cardPack: CardPack) {
        if (cardPack.link.isNotBlank()) viewModelScope.launch(Dispatchers.IO) {
            val res = _sharingHub.getCardpackContents(cardPack.link)
            res.flow.collect { repoRes ->
                withContext(Dispatchers.Main) {
                    when (repoRes) {
                        is RepoResult.Success<List<CardDeck>> -> _fetchedPack.value = repoRes.data
                        is RepoResult.Error<*> -> evFetchFailed.ignite("${repoRes.code} ${repoRes.message}")
                    }
                }
            }
        } else {
            evFetchFailed.ignite("${R.string.err_repo_linkError}")
        }
    }
    //endregion

    //region Deck fetching (information and cards list for import)
    private val _fetchedDeck = MutableLiveData<CardDeck>()
    val fetchedDeck: LiveData<CardDeck> = _fetchedDeck
    fun fetchCarddeckContent(deck: CardDeck) {
        _fetchedDeck.value = deck
        if (deck.link.isBlank()) evFetchFailed.ignite("${R.string.err_repo_linkError}")
        else viewModelScope.launch(Dispatchers.IO) {
            val res = _sharingHub.getCarddeckContent(deck.link)
            res.flow.collect { repoRes ->
                withContext(Dispatchers.Main) {
                    when (repoRes) {
                        is RepoResult.Success<CardDeck> -> _fetchedDeck.value = repoRes.data
                        is RepoResult.Error<*> -> evFetchFailed.ignite("${repoRes.code} ${repoRes.message}")
                    }
                }
            }
        }
    }
    //endregion

    companion object {
        class ViewModelFactory(
            private val sharingHub: SharingHub
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CardSharingViewModel::class.java)) {
                    return CardSharingViewModel(sharingHub) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}