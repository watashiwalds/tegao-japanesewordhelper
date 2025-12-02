package com.tegaoteam.application.tegao.ui.learning.cardlearn.model

import com.tegaoteam.application.tegao.domain.model.CardRepeat
import com.tegaoteam.application.tegao.utils.Time

data class LearnCardInfo(
    val cardId: Long,
    val status: Int
) {
    companion object {
        fun fromCardRepeat(rpt: CardRepeat) = LearnCardInfo(
            cardId = rpt.cardId,
            status = if (rpt.nextRepeat == null)
                STATUS_NEW
            else if (Time.absoluteTimeDifferenceBetween(rpt.nextRepeat, Time.getTodayMidnightTimestamp(), Time.DIFF_DAY) <= 0)
                STATUS_DUE
            else
                STATUS_FIN
        )
        
        const val STATUS_NEW = 0
        const val STATUS_DUE = 1
        const val STATUS_FIN = 2
    }
}