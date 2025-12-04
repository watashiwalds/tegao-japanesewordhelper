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
            _learningRepo.streakLaunchCheck()
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
    private val _groupCardsStatus = mutableMapOf<Long, LiveData<List<Pair<Long, Int>>>>()
    val groupCardsStatus: Map<Long, LiveData<List<Pair<Long, Int>>>> = _groupCardsStatus
    fun fetchCardsTypeOfGroup(groupId: Long): LiveData<List<Pair<Long, Int>>> {
        if (_groupCardsStatus[groupId] != null) return _groupCardsStatus[groupId]!!
        val liveData = _learningRepo.getCardRepeatsByGroupId(groupId).asFlow().map { repeats ->
            repeats.map { repeat -> Pair(
                repeat.cardId,
                if (repeat.nextRepeat == null)
                    CARDSTATUS_NEW
                else if (Time.absoluteTimeDifferenceBetween(Time.getTodayMidnightTimestamp(), repeat.nextRepeat, Time.DIFF_DAY) <= 0)
                    CARDSTATUS_DUE
                else
                    CARDSTATUS_LEARNED
            ) }
        }.asLiveData()
        _groupCardsStatus[groupId] = liveData
        return liveData
    }
    //end region

    companion object {
        const val CARDSTATUS_NEW = 0
        const val CARDSTATUS_LEARNED = 1
        const val CARDSTATUS_DUE = 2

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