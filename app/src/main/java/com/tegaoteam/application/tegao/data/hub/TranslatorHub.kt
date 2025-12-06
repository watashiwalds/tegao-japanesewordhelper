package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.data.config.TranslatorConfig
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.domain.model.Translator
import com.tegaoteam.application.tegao.domain.repo.TranslatorRepo

class TranslatorHub: TranslatorRepo {
    private fun getTranslatorApis() = TranslatorConfig.getTranslatorApis()
    override fun getAvailableTranslators(): List<Translator> = TranslatorConfig.getAvailableTranslators()

    override suspend fun translate(
        translatorId: String,
        text: String,
        sourceLang: Translator.Companion.Language,
        transLang: Translator.Companion.Language
    ): RepoResult<String> {
        val requestedApi = getTranslatorApis().find { it.translator?.id == translatorId }
        if (requestedApi == null) {
            return RepoResult.Error<String>(
                code = 404,
                message = "Requested translatorId doesn't match with any available ones"
            )
        }
        return requestedApi.translate(
            text = text,
            sourceLang = sourceLang,
            transLang = transLang
        )
    }
}