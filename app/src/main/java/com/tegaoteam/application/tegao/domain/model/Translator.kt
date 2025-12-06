package com.tegaoteam.application.tegao.domain.model

data class Translator(
    val id: String,
    val name: String,
    val supportedSourceLang: List<Language>,
    val supportedTransLang: List<Language>
) {
    companion object {
        enum class Language {
            JAPANESE,
            VIETNAMESE,
            ENGLISH
        }
    }
}