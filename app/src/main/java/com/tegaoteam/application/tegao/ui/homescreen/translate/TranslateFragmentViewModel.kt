package com.tegaoteam.application.tegao.ui.homescreen.translate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.domain.model.Translator
import com.tegaoteam.application.tegao.domain.repo.TranslatorRepo
import com.tegaoteam.application.tegao.ui.shared.FetchedConfigs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class TranslateFragmentViewModel(private val translatorRepo: TranslatorRepo): ViewModel() {
    val isHandWritingEnable = FetchedConfigs.isHandwritingEnabled

    //region Fragment's mutable config in the session
    var translator: Translator? = null
    var sourceText: String? = null
    var sourceLang: Translator.Companion.Language? = null
    var transLang: Translator.Companion.Language? = null
    //endregion

    private val _translateResult = MutableLiveData<String>()
    val translateResult: LiveData<String> = _translateResult
    fun requestTranslation() {
        if (translator == null || sourceText == null || sourceLang == null || transLang == null) {
            _translateResult.value = ""
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val res = translatorRepo.translate(
                translatorId = translator!!.id,
                text = sourceText!!,
                sourceLang = sourceLang!!,
                transLang = transLang!!
            )
            withContext(Dispatchers.Main) {
                when (res) {
                    is RepoResult.Success<String> -> _translateResult.value = res.data
                    is RepoResult.Error<*> -> _translateResult.value = "${res.code} ${res.message}"
                }
            }
        }
    }

    companion object {
        class ViewModelFactory(
            private val translatorRepo: TranslatorRepo
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TranslateFragmentViewModel::class.java)) {
                    return TranslateFragmentViewModel(translatorRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}