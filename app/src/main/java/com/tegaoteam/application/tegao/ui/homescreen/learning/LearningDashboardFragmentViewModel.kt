package com.tegaoteam.application.tegao.ui.homescreen.learning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.data.model.asFlow
import com.tegaoteam.application.tegao.domain.model.CardGroup
import com.tegaoteam.application.tegao.domain.model.CardRepeat
import com.tegaoteam.application.tegao.domain.repo.LearningRepo
import com.tegaoteam.application.tegao.utils.EventBeacon
import com.tegaoteam.application.tegao.utils.Time
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.floor

class LearningDashboardFragmentViewModel(private val _learningRepo: LearningRepo): ViewModel() {
    val cardGroups: LiveData<List<CardGroup>> = _learningRepo.getCardGroups().asFlow().asLiveData()
    val dueCardIds: LiveData<List<Long>> = _learningRepo.getTodayDueCardIds(Time.getCurrentTimestamp().toString()).asFlow().asLiveData()

    //region Display global information on dashboard
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

    //region Fetching repeatsByGroup and storing for later use in ViewModel
    private val _repeatsByGroup: MutableMap<Long, List<Pair<Long, Int>>> = mutableMapOf()
    val repeatsByGroup: Map<Long, List<Pair<Long, Int>>> = _repeatsByGroup
    val eventRepeatsByGroupUpdated = EventBeacon()
    private fun fetchRepeatsByGroup() {
        viewModelScope.launch {
            val groups = (cardGroups.value?: _learningRepo.getCardGroups().asFlow().first()).map { it.groupId }
            val fetchMap = mutableMapOf<Long, List<Pair<Long, Int>>>()
            groups.forEach { groupId ->
                fetchMap[groupId] = _learningRepo.getCardRepeatsByGroupId(groupId).asFlow().map { it.map { rpt -> Pair(
                    rpt.cardId,
                    if (rpt.nextRepeat == null)
                        CARDSTATUS_NEW
                    else if (Time.absoluteTimeDifferenceBetween(rpt.nextRepeat, Time.getTodayMidnightTimestamp(), Time.DIFF_DAY) <= 0)
                        CARDSTATUS_DUE
                    else
                        CARDSTATUS_LEARNED
                ) } }.first()
            }
            withContext(Dispatchers.Main) {
                fetchMap.keys.forEach { _repeatsByGroup[it] = fetchMap[it]!! }
                eventRepeatsByGroupUpdated.ignite()
            }
        }
    }
    //endregion

    //region Display groups with cards count and group-only reepatIds fetching
    private val _dashboardGroups = MutableLiveData<List<LearningInfoDataClasses.DashboardCardGroupInfo>>()
    val dashboardGroups: LiveData<List<LearningInfoDataClasses.DashboardCardGroupInfo>> = _dashboardGroups
    fun composeDashboardGroup() {
        viewModelScope.launch(Dispatchers.Default) {
            val groups = cardGroups.value?: _learningRepo.getCardGroups().asFlow().first()
            val composed = groups.map {
                val groupRepeats = repeatsByGroup[it.groupId]
                val new = groupRepeats?.count { rpt -> rpt.second == CARDSTATUS_NEW }?: 0
                val due = groupRepeats?.count { rpt -> rpt.second == CARDSTATUS_DUE }?: 0
                val progress = floor((((groupRepeats?.size?: 1) - new.toDouble()) / (groupRepeats?.size?: 1)) * 100).toInt()
                LearningInfoDataClasses.DashboardCardGroupInfo(
                    groupEntry = it,
                    newCardsCount = new,
                    dueCardsCount = due,
                    clearProgress = progress
                )
            }
            withContext(Dispatchers.Main) {
                _dashboardGroups.value = composed
            }
        }
    }
    //endregion

    fun refreshData() {
        fetchDashboardInfo()
        fetchRepeatsByGroup()
    }

    companion object {
        private const val CARDSTATUS_NEW = 0
        private const val CARDSTATUS_LEARNED = 1
        private const val CARDSTATUS_DUE = 2

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