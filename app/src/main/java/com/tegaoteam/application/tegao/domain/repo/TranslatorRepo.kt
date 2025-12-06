package com.tegaoteam.application.tegao.domain.repo

import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.domain.model.Translator

interface TranslatorRepo {
    fun getAvailableTranslators(): List<Translator>

    suspend fun translate(translatorId: String, text: String, sourceLang: Translator.Companion.Language, transLang: Translator.Companion.Language): RepoResult<String>
}