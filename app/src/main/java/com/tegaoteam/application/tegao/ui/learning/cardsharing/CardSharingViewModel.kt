package com.tegaoteam.application.tegao.ui.learning.cardsharing

import android.net.Uri
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
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.domain.model.CardGroup
import com.tegaoteam.application.tegao.domain.repo.LearningRepo
import com.tegaoteam.application.tegao.utils.EventBeacon
import com.tegaoteam.application.tegao.utils.Time
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CardSharingViewModel(private val _sharingHub: SharingHub, private val _learningRepo: LearningRepo): ViewModel() {
    val evFetchFailed = EventBeacon()
    val packSources = _sharingHub.getSavedCardpackSources()

    //region Card pack fetching (get deck list from online repo)
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

    private val _fetchedDeck = MutableLiveData<CardDeck>()
    val fetchedDeck: LiveData<CardDeck> = _fetchedDeck
    //region Deck fetching from online repo
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
    //region Deck parsing from selected Json file
    fun parsingJsonFile(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = _sharingHub.readCardDeckFromLocalJson(uri)
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

    //region Importing cards with progress update
    val evImportFinished = EventBeacon()
    private val _importProgress = MutableLiveData<String>()
    val importProgress: LiveData<String> = _importProgress
    fun importCardDeck(group: CardGroup, cards: List<CardEntry>) {
        _importProgress.value = "0"
        viewModelScope.launch(Dispatchers.IO) {
            val impGroupId = _learningRepo.upsertCardGroup(group.copy(groupId = 0))
            if (impGroupId == -1L) {
                withContext(Dispatchers.Main) {
                    evImportFinished.ignite("${R.string.err_deckImport_cantCreateGroup}")
                }
            } else {
                var progCounter = 0
                cards.forEach { card ->
                    val cardImportRes = _learningRepo.upsertCard(card.copy(cardId = 0, groupId = impGroupId, dateCreated = Time.getCurrentTimestamp().toString()))
                    if (cardImportRes > 0) withContext(Dispatchers.Main) { _importProgress.value = (++progCounter).toString() }
                }
                withContext(Dispatchers.Main) {
                    if (progCounter == cards.size) evImportFinished.ignite()
                    else evImportFinished.ignite("($progCounter/${cards.size})")
                }
            }
        }
    }
    //endregion

    companion object {
        class ViewModelFactory(
            private val sharingHub: SharingHub,
            private val learningRepo: LearningRepo
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CardSharingViewModel::class.java)) {
                    return CardSharingViewModel(sharingHub, learningRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}