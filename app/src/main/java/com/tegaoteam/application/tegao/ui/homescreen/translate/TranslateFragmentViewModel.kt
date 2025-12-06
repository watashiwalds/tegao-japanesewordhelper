package com.tegaoteam.application.tegao.ui.homescreen.translate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.domain.repo.AddonRepo

class TranslateFragmentViewModel(private val addonRepo: AddonRepo): ViewModel() {
    companion object {
        class ViewModelFactory(
            private val addonRepo: AddonRepo
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TranslateFragmentViewModel::class.java)) {
                    return TranslateFragmentViewModel(addonRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}