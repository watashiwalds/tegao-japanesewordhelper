package com.tegaoteam.application.tegao.ui.learning.cardsharing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.data.database.cardpack.CardDeck
import com.tegaoteam.application.tegao.data.database.cardpack.CardPack
import com.tegaoteam.application.tegao.data.hub.SharingHub
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.utils.EventBeacon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class CardSharingViewModel(private val _sharingHub: SharingHub): ViewModel() {

    val packSources = _sharingHub.getSavedCardpackSources()

    val evFetchPackFailed = EventBeacon()
    private val _fetchedPack = MutableLiveData<List<CardDeck>>()
    val fetchedPack: LiveData<List<CardDeck>> = _fetchedPack
    fun fetchCardpackContents(cardPack: CardPack) {
        if (cardPack.link.isNotBlank()) viewModelScope.launch(Dispatchers.IO) {
            val res = _sharingHub.getCardpackContents(cardPack.link)
            res.flow.collect { repoRes ->
                withContext(Dispatchers.Main) {
                    when (repoRes) {
                        is RepoResult.Success<List<CardDeck>> -> _fetchedPack.value = repoRes.data
                        is RepoResult.Error<*> -> evFetchPackFailed.ignite("${repoRes.code} ${repoRes.message}")
                    }
                }
            }
        }
    }

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