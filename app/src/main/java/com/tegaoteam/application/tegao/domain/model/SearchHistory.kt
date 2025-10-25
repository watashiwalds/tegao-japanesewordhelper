package com.tegaoteam.application.tegao.domain.model

data class SearchHistory(
    val type: Int,
    val keyword: String,
    val searchDate: String
) {
    companion object {
        const val TYPE_WORD = 0
        const val TYPE_KANJI = 1
    }
}