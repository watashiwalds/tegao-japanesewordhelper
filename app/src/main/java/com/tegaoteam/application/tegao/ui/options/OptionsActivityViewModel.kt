package com.tegaoteam.application.tegao.ui.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.OnlineServiceHub
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.ui.component.generics.listnavigation.ListNavigationItemInfo
import com.tegaoteam.application.tegao.utils.EventBeacon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OptionsActivityViewModel(private val _onlineServiceHub: OnlineServiceHub): ViewModel() {
    val navigationList = listOf(
        ListNavigationItemInfo(
            labelResId = R.string.options_setting_label,
            directionId = R.id.settingListFragment,
            detailResId = R.string.options_setting_description,
            iconResId = R.drawable.ftc_round_setting_128
        ),
        ListNavigationItemInfo(
            labelResId = R.string.options_info_label,
            directionId = 0,
            detailResId = R.string.options_info_description,
            iconResId = R.drawable.ftc_round_info_128
        )
    )

    val evNotifiedToken = EventBeacon()
    fun notifyLoginTokenToServer(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = _onlineServiceHub.notifyLoginTokenToServer(token)
            withContext(Dispatchers.Main) {
                when (res) {
                    is RepoResult.Error<*> -> evNotifiedToken.ignite("${res.code} ${res.message}")
                    is RepoResult.Success<Nothing> -> {}
                }
            }
        }
    }

    companion object {
        class ViewModelFactory(
            private val onlineServiceHub: OnlineServiceHub
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(OptionsActivityViewModel::class.java)) {
                    return OptionsActivityViewModel(onlineServiceHub) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}