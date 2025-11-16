package com.tegaoteam.application.tegao.ui.setting.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.domain.repo.SearchHistoryRepo
import com.tegaoteam.application.tegao.ui.setting.model.ConfigEntryItem
import com.tegaoteam.application.tegao.utils.AppToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingHistoryViewModel(private val searchHistoryRepo: SearchHistoryRepo): ViewModel() {
    val historySettings = listOf(
        ConfigEntryItem(
            labelResId = R.string.setting_history_label_deleteAll,
            descriptionResId = R.string.setting_history_detail_deleteAll,
            type = ConfigEntryItem.Companion.Type.CONFIRMATION,
            clickListener = {
                viewModelScope.launch(Dispatchers.Default) {
                    val count = searchHistoryRepo.deleteAll()
                    withContext(Dispatchers.Main) {
                        AppToast.show(
                            String.format(TegaoApplication.instance.getString(R.string.setting_history_result_deleteAll), count),
                            AppToast.LENGTH_SHORT
                        )
                    }
                }
            }
        )
    )

    companion object {
        class ViewModelFactory(
            private val searchHistoryRepo: SearchHistoryRepo
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SettingHistoryViewModel::class.java)) {
                    return SettingHistoryViewModel(searchHistoryRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}