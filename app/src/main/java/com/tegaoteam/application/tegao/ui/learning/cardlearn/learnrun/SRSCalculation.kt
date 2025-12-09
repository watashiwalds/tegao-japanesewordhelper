package com.tegaoteam.application.tegao.ui.learning.cardlearn.learnrun

import androidx.lifecycle.MutableLiveData
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.domain.model.CardRepeat
import com.tegaoteam.application.tegao.ui.component.learningcard.LearningCardBindingHelper
import com.tegaoteam.application.tegao.utils.Time
import com.tegaoteam.application.tegao.utils.getStringFromAppRes
import kotlin.math.floor

class SRSCalculation {
    val srsDate_forget = MutableLiveData<String>()
    val srsDate_hard = MutableLiveData<String>()
    val srsDate_good = MutableLiveData<String>()
    val srsDate_easy = MutableLiveData<String>()

    private val decimalInterval = mutableMapOf<Int, Double>()

    private lateinit var currentRepeat: CardRepeat
    fun calculateRepeat(rpt: CardRepeat) {
        currentRepeat = rpt
        var repeatGap = Time.absoluteTimeDifferenceBetween(rpt.lastRepeat, rpt.nextRepeat?: rpt.lastRepeat, Time.DIFF_DAY)
        val easeFactor = rpt.easeFactor

        // New card - Old card
        if (rpt.nextRepeat.isNullOrEmpty()) {
            decimalInterval[RATING_FORGET] = 0.0
            decimalInterval[RATING_HARD] = 0.6
            decimalInterval[RATING_GOOD] = 1.0
            decimalInterval[RATING_EASY] = 4.0
        } else {
            if (repeatGap < 1) repeatGap = 1
            decimalInterval[RATING_FORGET] = 0.0
            decimalInterval[RATING_HARD] = repeatGap * 0.8
            decimalInterval[RATING_GOOD] = repeatGap * (easeFactor - 0.5)
            decimalInterval[RATING_EASY] = repeatGap * easeFactor * EASY_BONUS
        }

        generateText()
    }

    private fun generateText() {
        val txtF = decimalInterval[RATING_FORGET]!!.let { if (it < 1.0) "5m" else "${floor(it).toLong()}d" }
        val txtH = decimalInterval[RATING_HARD]!!.let { if (it < 1.0) "10m" else "${floor(it).toLong()}d" }
        val txtG = "${floor(decimalInterval[RATING_GOOD]!!).toLong()}d"
        val txtE = "${floor(decimalInterval[RATING_EASY]!!).toLong()}d"
        srsDate_forget.value = txtF
        srsDate_hard.value = txtH
        srsDate_good.value = txtG
        srsDate_easy.value = txtE
    }

    fun makeRepeatOfRating(rating: Int): CardRepeat {
        val plc = currentRepeat.copy()
        if (rating !in listOf(RATING_FORGET, RATING_HARD, RATING_GOOD, RATING_EASY)) return plc

        val interval = floor(decimalInterval[rating]!!)
        var updEaseFactor = plc.easeFactor + when (rating) {
            RATING_FORGET -> -0.3
            RATING_HARD -> -0.1
            RATING_GOOD -> 0.1
            RATING_EASY -> 0.2
            else -> 0.0
        }
        if (updEaseFactor < 1.6) updEaseFactor = 1.6

        plc.apply {
            lastRepeat = Time.getTodayMidnightTimestamp().toString()
            nextRepeat = Time.addDays(Time.getTodayMidnightTimestamp(), interval.toLong()).toString()
            easeFactor = updEaseFactor
        }

        return plc
    }

    private fun formatText(resId: Int, s: String) = String.format(getStringFromAppRes(resId), s)

    companion object {
        const val EASY_BONUS = 2.0

        const val RATING_EASY = LearningCardBindingHelper.COLLIDE_NORTH
        const val RATING_GOOD = LearningCardBindingHelper.COLLIDE_WEST
        const val RATING_HARD = LearningCardBindingHelper.COLLIDE_EAST
        const val RATING_FORGET = LearningCardBindingHelper.COLLIDE_SOUTH
    }
}