package com.tegaoteam.application.tegao.ui.homescreen.learning

import com.tegaoteam.application.tegao.domain.model.CardGroup

class LearningBindingDataClasses {
    data class DashboardBinding(
        val dueCardCount: Int,
        val streakCount: Int,
        val cardGroupCount: Int
    )
    data class DashboardCardGroupBinding(
        val groupEntry: CardGroup,
        val newCardsCount: Int,
        val dueCardsCount: Int,
        val clearProgress: Int,
        val onStartLearnClickListener: ((Long) -> Unit)? = null,
        val onGroupClickListener: ((Long) -> Unit)? = null
    )
    data class QuickCrudItemBinding(
        val id: Long,
        val label: String,
        val quickInfo: String,
        val onEditQabClickListener: ((Long) -> Unit)? = null,
        val onDeleteQabClickListener: ((Long) -> Unit)? = null,
        val onItemClickListener: ((Long) -> Unit)? = null
    )
}