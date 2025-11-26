package com.tegaoteam.application.tegao.ui.setting.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.domain.repo.StorageRepo
import com.tegaoteam.application.tegao.ui.setting.model.ConfigEntryItem
import com.tegaoteam.application.tegao.utils.AppToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingStorageViewModel(private val storageRepo: StorageRepo): ViewModel() {
    val storageSettings = listOf(
        ConfigEntryItem(
            labelResId = R.string.setting_storage_label_deleteDictionaryCaches,
            descriptionResId = R.string.setting_storage_detail_deleteDictionaryCaches,
            type = ConfigEntryItem.Companion.Type.CONFIRMATION,
            clickListener = {
                viewModelScope.launch(Dispatchers.Default) {
                    val count = storageRepo.deleteSearchCaches()
                    withContext(Dispatchers.Main) {
                        AppToast.show(
                            String.format(TegaoApplication.instance.getString(R.string.setting_storage_result_deleteDictionaryCaches), count),
                            AppToast.LENGTH_SHORT
                        )
                    }
                }
            }
        ),
        ConfigEntryItem(
            labelResId = R.string.setting_storage_label_deleteLearningDatabase,
            descriptionResId = R.string.setting_storage_detail_deleteLearningDatabase,
            type = ConfigEntryItem.Companion.Type.CONFIRMATION,
            clickListener = {
                viewModelScope.launch(Dispatchers.Default) {
                    val count = storageRepo.deleteLearningCardDatabase()
                    withContext(Dispatchers.Main) {
                        AppToast.show(
                            String.format(TegaoApplication.instance.getString(R.string.setting_storage_result_deleteLearningDatabase), count),
                            AppToast.LENGTH_SHORT
                        )
                    }
                }
            }
        )
    )

    companion object {
        class ViewModelFactory(
            private val storageRepo: StorageRepo
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SettingStorageViewModel::class.java)) {
                    return SettingStorageViewModel(storageRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}