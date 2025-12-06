package com.tegaoteam.application.tegao.data.config

import com.tegaoteam.application.tegao.data.network.translator.GoogleTranslateApi
import com.tegaoteam.application.tegao.data.network.translator.TranslatorApi
import com.tegaoteam.application.tegao.domain.model.Translator
import com.tegaoteam.application.tegao.domain.model.Translator.Companion.Language as Language

object TranslatorConfig {
    val TRANSLATOR_GOOGLETRANSLATE = Translator(
        id = "google_translate",
        name = "Google Translate",
        supportedSourceLang = listOf(Language.JAPANESE, Language.VIETNAMESE, Language.ENGLISH),
        supportedTransLang = listOf(Language.JAPANESE, Language.VIETNAMESE, Language.ENGLISH)
    )

    fun getTranslatorApis() = listOf<TranslatorApi>(GoogleTranslateApi.api)
    fun getAvailableTranslators() = listOf(TRANSLATOR_GOOGLETRANSLATE)
}