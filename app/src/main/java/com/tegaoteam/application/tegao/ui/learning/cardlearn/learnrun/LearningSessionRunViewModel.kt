package com.tegaoteam.application.tegao.ui.learning.cardlearn.learnrun

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.domain.model.CardRepeat
import timber.log.Timber
import kotlin.math.min
import kotlin.random.Random

class LearningSessionRunViewModel: ViewModel() {
    var sessionMode: Int = -1

    //region Init functions and session data (entry and repeat)
    private val _sessionCards = mutableListOf<CardEntry>()
    private val _sessionRepeats = mutableMapOf<Long, CardRepeat>()
    private val _sessionCardIds = mutableListOf<Long>()
    fun submitSessionData(cards: List<CardEntry>, repeatList: List<CardRepeat>) {
        if (_sessionCards.isEmpty()) {
            _sessionCards.addAll(cards)
            repeatList.forEach {
                _sessionRepeats[it.cardId] = it
                _sessionCardIds.add(it.cardId)
            }
            updateProgress(PROGRESS_INITVALUES)
        }
    }
    //endregion

    //region During learning functions
    fun getRepeatByCardId(cardId: Long): CardRepeat? = _sessionRepeats[cardId]
    fun nextCardOrNull(): CardEntry? = _sessionCardIds.firstOrNull()?.let { id -> _sessionCards.find {it.cardId == id} }
    fun popTopCard(): CardEntry? {
        val res = if (_sessionCardIds.isNotEmpty()) {
            val picked = _sessionCardIds.removeAt(0)
            _sessionCards.find{ it.cardId == picked }
        } else null
        res?.let {
            val popCardStatus = _reviewStatus.removeAt(0)
            updateProgress(if (popCardStatus == CARDSTATUS_SCHEDULED) PROGRESS_DECKPOP_SCHEDULED else PROGRESS_DECKPOP_REPEATED )
            poppedCardStatus = popCardStatus
        }
        return res
    }
    //endregion

    //region Information text during learning (counter, etc...)
    private val _sessionProgress = MutableLiveData<String>()
    val sessionProgress: LiveData<String> = _sessionProgress
    private val _reviewStatus = mutableListOf<Int>()
    var poppedCardStatus = CARDSTATUS_SCHEDULED
        private set

    private var originSize = 0
    private var onReview = 0

    var repeatedCardCount = 0
        private set
    var rememberedCardCount = 0
        private set

    private fun updateProgress(status: Int) {
        when (status) {
            PROGRESS_INITVALUES -> {
                originSize = _sessionCards.size
                for (i in 1..originSize) _reviewStatus.add(CARDSTATUS_SCHEDULED)
            }
            PROGRESS_DECKPOP_SCHEDULED -> {
                originSize--
                onReview++
            }
            PROGRESS_RATECARD -> {
                onReview--
            }
            PROGRESS_CARDREPEATED -> {
                onReview++
                repeatedCardCount++
            }
            PROGRESS_CARDREMEMBERED -> {
                rememberedCardCount++
            }
        }
        _sessionProgress.value = "$originSize  •  $onReview  •  ${repeatedCardCount + rememberedCardCount}"
    }
    //endregion

    //region Sync with parent viewModel on database changes
    private val _sessionLearnedRepeats = mutableMapOf<Long, CardRepeat>()
    val sessionLearnedRepeats: Map<Long, CardRepeat> = _sessionLearnedRepeats

    fun updateRepeat(rpt: CardRepeat) {
        updateProgress(PROGRESS_RATECARD)

        if (rpt.lastRepeat < rpt.nextRepeat!!)
            updateProgress(PROGRESS_CARDREMEMBERED)
        else {
            val queueSize = _sessionCardIds.size
            val addAt = if (queueSize == 0) 0 else min(Random.nextInt(1, 5), queueSize)
            _sessionCardIds.add(addAt, rpt.cardId)
            _reviewStatus.add(addAt, CARDSTATUS_REPEATED)
            updateProgress(PROGRESS_CARDREPEATED)
        }

        _sessionRepeats[rpt.cardId] = rpt
        _sessionLearnedRepeats[rpt.cardId] = rpt
    }
    //endregion

    companion object {
        private const val PROGRESS_INITVALUES = -1
        private const val PROGRESS_DECKPOP_SCHEDULED = 0
        private const val PROGRESS_DECKPOP_REPEATED = 1
        private const val PROGRESS_RATECARD = 2
        private const val PROGRESS_CARDREPEATED = 3
        private const val PROGRESS_CARDREMEMBERED = 4

        const val CARDSTATUS_SCHEDULED = 0
        const val CARDSTATUS_REPEATED = 1
    }
}