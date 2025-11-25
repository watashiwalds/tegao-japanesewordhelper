package com.tegaoteam.application.tegao.ui.homescreen.learning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.data.model.asFlow
import com.tegaoteam.application.tegao.domain.model.CardGroup
import com.tegaoteam.application.tegao.domain.repo.LearningRepo
import com.tegaoteam.application.tegao.utils.Time
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LearningDashboardFragmentViewModel(private val _learningRepo: LearningRepo): ViewModel() {
    val cardGroups: LiveData<List<CardGroup>> = _learningRepo.getCardGroups().asFlow().asLiveData()
    val dueCardIds: LiveData<List<Long>> = _learningRepo.getTodayDueCardIds(Time.getCurrentTimestamp().toString()).asFlow().asLiveData()

    //region Display information for dashboard
    private val _dashboardInfo = MutableLiveData<LearningInfoDataClasses.DashboardInfo>()
    val dashboardInfo: LiveData<LearningInfoDataClasses.DashboardInfo> = _dashboardInfo
    fun fetchDashboardInfo() {
        viewModelScope.launch {
            val groupsCount = cardGroups.value?.size?: _learningRepo.getCardGroups().asFlow().first().size
            val dueCardsCount = dueCardIds.value?.size?: _learningRepo.getTodayDueCardIds(Time.getCurrentTimestamp().toString()).asFlow().first().size
            val currentStreak = _learningRepo.currentStreak().asFlow().first()
            val highestStreak = _learningRepo.highestStreak().asFlow().first()
            withContext(Dispatchers.Main) {
                _dashboardInfo.value = LearningInfoDataClasses.DashboardInfo(
                    dueCardCount = dueCardsCount,
                    currentStreak = currentStreak.toInt(),
                    highestStreak = highestStreak.toInt(),
                    cardGroupCount = groupsCount
                )
            }
        }
    }
    //endregion

    companion object {
        class ViewModelFactory(
            private val learningRepo: LearningRepo
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(LearningDashboardFragmentViewModel::class.java)) {
                    return LearningDashboardFragmentViewModel(learningRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}