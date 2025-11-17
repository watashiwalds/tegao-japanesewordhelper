package com.tegaoteam.application.tegao.domain.model

data class SearchHistory(
    val type: Int,
    val keyword: String,
    val searchDate: String
) {
    companion object {
        const val TYPE_WORD = Word.TYPE_VALUE
        const val TYPE_KANJI = Kanji.TYPE_VALUE
    }
}