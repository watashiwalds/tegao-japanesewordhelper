package com.tegaoteam.application.tegao.ui.homescreen.learning

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
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
        val newCardsCount: LiveData<Int>,
        val dueCardsCount: LiveData<Int>,
        val clearProgress: LiveData<Int>,
        var onStartLearnClickListener: ((Long) -> Unit)? = null,
        var onGroupClickListener: ((Long) -> Unit)? = null,
        val lifecycleOwner: LifecycleOwner? = null
    )
    data class QuickCrudItemInfo(
        val id: Long,
        val label: String,
        val quickInfo: LiveData<String>,
        var onEditQabClickListener: ((Long) -> Unit)? = null,
        var onDeleteQabClickListener: ((Long) -> Unit)? = null,
        var onItemClickListener: ((Long) -> Unit)? = null,
        val lifecycleOwner: LifecycleOwner? = null
    )
}