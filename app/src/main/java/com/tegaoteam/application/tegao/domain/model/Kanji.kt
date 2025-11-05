package com.tegaoteam.application.tegao.domain.model

data class Kanji(
    val id: Int,
    val character: String,
    var kunyomi: String? = null,
    var onyomi: String? = null,
    var composites: List<Pair<String, String?>>? = null,
    val meaning: String,
    var tags: List<Pair<String, String>>? = null,
    var additionalInfo: List<Pair<String, String>>? = null,
) {
    companion object {
        fun default() = Kanji(
            id = 0,
            character = "",
            meaning = ""
        )
    }
}