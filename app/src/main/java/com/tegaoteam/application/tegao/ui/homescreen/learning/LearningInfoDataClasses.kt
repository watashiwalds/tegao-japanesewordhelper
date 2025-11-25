package com.tegaoteam.application.tegao.ui.homescreen.learning

import com.tegaoteam.application.tegao.domain.model.CardGroup

class LearningInfoDataClasses {
    data class DashboardInfo(
        val dueCardCount: Int,
        val currentStreak: Int,
        val highestStreak: Int,
        val cardGroupCount: Int
    )
    data class DashboardCardGroupInfo(
        val groupEntry: CardGroup,
        val newCardsCount: Int,
        val dueCardsCount: Int,
        val clearProgress: Int,
        val onStartLearnClickListener: ((Long) -> Unit)? = null,
        val onGroupClickListener: ((Long) -> Unit)? = null
    )
    data class QuickCrudItemInfo(
        val id: Long,
        val label: String,
        val quickInfo: String,
        val onEditQabClickListener: ((Long) -> Unit)? = null,
        val onDeleteQabClickListener: ((Long) -> Unit)? = null,
        val onItemClickListener: ((Long) -> Unit)? = null
    )
}