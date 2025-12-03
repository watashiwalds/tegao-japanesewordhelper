package com.tegaoteam.application.tegao.ui.learning.cardlearn.learnrun

import androidx.lifecycle.MutableLiveData
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.domain.model.CardRepeat
import com.tegaoteam.application.tegao.utils.Time
import com.tegaoteam.application.tegao.utils.getStringFromAppRes
import kotlin.math.floor

class SRSCalculation {
    val srsText_forget = MutableLiveData<String>()
    val srsText_hard = MutableLiveData<String>()
    val srsText_good = MutableLiveData<String>()
    val srsText_easy = MutableLiveData<String>()

    private val decimalInterval = mutableMapOf<Int, Double>()

    private lateinit var currentRepeat: CardRepeat
    fun calculateRepeat(rpt: CardRepeat) {
        currentRepeat = rpt
        val repeatGap = Time.absoluteTimeDifferenceBetween(rpt.lastRepeat, rpt.nextRepeat, Time.DIFF_DAY)
        val easeFactor = rpt.easeFactor

        // New card - Old card
        if (rpt.nextRepeat.isNullOrEmpty()) {
            decimalInterval[RATING_FORGET] = 0.0
            decimalInterval[RATING_HARD] = 0.6
            decimalInterval[RATING_GOOD] = 1.0
            decimalInterval[RATING_EASY] = 4.0
        } else {
            decimalInterval[RATING_FORGET] = 0.0
            decimalInterval[RATING_HARD] = repeatGap * 1.2
            decimalInterval[RATING_GOOD] = repeatGap * easeFactor
            decimalInterval[RATING_EASY] = repeatGap * easeFactor * EASY_BONUS
        }

        generateText()
    }

    private fun generateText() {
        val txtF = decimalInterval[RATING_FORGET]!!.let { if (it < 1.0) "5m" else "${floor(it)}d" }
        val txtH = decimalInterval[RATING_HARD]!!.let { if (it < 1.0) "10m" else "${floor(it)}d" }
        val txtG = "${floor(decimalInterval[RATING_GOOD]!!)}d"
        val txtE = "${floor(decimalInterval[RATING_EASY]!!)}d"
        srsText_forget.value = formatText(R.string.card_learn_rating_forget, txtF)
        srsText_hard.value = formatText(R.string.card_learn_rating_hard, txtH)
        srsText_good.value = formatText(R.string.card_learn_rating_good, txtG)
        srsText_easy.value = formatText(R.string.card_learn_rating_easy, txtE)
    }

    fun makeRepeatOfRating(rating: Int): CardRepeat {
        val plc = currentRepeat.copy()
        if (rating !in listOf(RATING_FORGET, RATING_HARD, RATING_GOOD, RATING_EASY)) return plc

        val interval = floor(decimalInterval[rating]!!)
        val updEaseFactor = plc.easeFactor + when (rating) {
            RATING_FORGET -> -0.2
            RATING_GOOD -> -0.15
            RATING_EASY -> 0.15
            else -> 0.0
        }

        plc.apply {
            lastRepeat = Time.getTodayMidnightTimestamp().toString()
            nextRepeat = Time.addDays(Time.getTodayMidnightTimestamp(), interval.toLong()).toString()
            easeFactor = updEaseFactor
        }

        return plc
    }

    private fun formatText(resId: Int, s: String) = String.format(getStringFromAppRes(resId), s)

    companion object {
        const val EASY_BONUS = 1.3

        const val RATING_FORGET = 0
        const val RATING_HARD = 1
        const val RATING_GOOD = 2
        const val RATING_EASY = 3
    }
}