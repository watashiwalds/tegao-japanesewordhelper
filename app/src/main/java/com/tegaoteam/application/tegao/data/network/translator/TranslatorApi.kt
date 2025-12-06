package com.tegaoteam.application.tegao.data.network.translator

import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.domain.model.Translator

interface TranslatorApi {
    val translator: Translator?
    suspend fun translate(text: String, sourceLang: Translator.Companion.Language, transLang: Translator.Companion.Language): RepoResult<String>
    fun onNoInternetAvailable(): RepoResult<Nothing>
}